package com.pjz.review.service.impl;

import cn.hutool.json.JSONUtil;
import com.pjz.review.entity.Shops;
import com.pjz.review.entity.vo.ShopVO;
import com.pjz.review.mapper.ShopsMapper;
import com.pjz.review.service.ShopService;
import com.pjz.review.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.pjz.review.utils.ExceptionsConstants.SHOP_NOT_EXITS;
import static com.pjz.review.utils.RedisConstants.*;

@Service
public class ShopServiceImpl implements ShopService {

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public ShopVO queryById(Long id) {

        // 从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 判断是否命中
        if (StringUtils.hasText(shopJson)) {
            return JSONUtil.toBean(shopJson, ShopVO.class);
        }

        // 判断是否为空字符串
        Assert.isNull(shopJson, SHOP_NOT_EXITS);

        // 没命中，到数据库中查询
        Shops shops = shopsMapper.getById(id);
        // 当数据库中没有查询到数据时，为了避免缓存穿透，也应该将空对象写回缓存里
        if (Objects.isNull(shops)) {
            // 将空对象写进缓存里
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            throw new IllegalArgumentException(SHOP_NOT_EXITS);
        }

        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shops, shopVO);

        // 将数据库中的数据写回redis，并且要通过设置超时时间，来保证超时剔除的兜底方案
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shopVO), CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return shopVO;
    }

    @Override
    @Transactional
    public Void updateById(ShopVO shopVO) {

        // 校验id是否存在
        Assert.notNull(shopVO.getId(), "商铺id不能为空");

        Shops shops = new Shops();
        BeanUtils.copyProperties(shopVO, shops);
        shops.setUpdatedAt(LocalDateTime.now());

        // 先更新数据库，后删除缓存可以尽可能降低线程安全带来的数据不一致问题
        shopsMapper.updateById(shops);

        // 删除缓存，而不是直接写回缓存，可以避免多次无效写操作
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shopVO.getId());

        return null;
    }
}
