package com.pjz.review.service.impl;

import cn.hutool.json.JSONUtil;
import com.pjz.review.entity.Shops;
import com.pjz.review.entity.vo.ShopVO;
import com.pjz.review.mapper.ShopsMapper;
import com.pjz.review.service.ShopService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.pjz.review.utils.ExceptionsConstants.SHOP_NOT_EXITS;
import static com.pjz.review.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.pjz.review.utils.RedisConstants.CACHE_SHOP_TTL;

@Service
public class ShopServiceImpl implements ShopService {

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ShopVO queryById(Long id) {

        // 从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 判断是否命中
        if (StringUtils.hasText(shopJson)) {
            return JSONUtil.toBean(shopJson, ShopVO.class);
        }

        // 没命中，到数据库中查询
        Shops shops = shopsMapper.getById(id);
        Assert.notNull(shops, SHOP_NOT_EXITS);

        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shops, shopVO);

        // 将数据库中的数据写回redis，并且要通过设置超时时间，来保证超时剔除的兜底方案
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shopVO), CACHE_SHOP_TTL, TimeUnit.MINUTES);

        return shopVO;
    }
}
