package com.zwj.zwjutils.net.callback;

import android.text.TextUtils;

import com.zwj.zwjutils.JsonUtil;
import com.zwj.zwjutils.net.bean.ResponseBean;

/**
 * Created by zwj on 2016/12/9.
 */

public abstract class ParseBeanCallBack<T> extends SimpleCallBack {

    private Class<T> clazz;

    public ParseBeanCallBack(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public void onSuccess(ResponseBean responseBean) {
        // TODO 还需处理一些类型转换出错等之类的错误，抛出异常
        String result = responseBean.getResult();
        if (!TextUtils.isEmpty(result)) {
            T obj = JsonUtil.getObject(result, clazz);
            onSuccess(responseBean, obj);
        }
    }

    public abstract void onSuccess(ResponseBean responseBean, T obj);
}
