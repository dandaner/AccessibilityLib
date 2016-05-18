package com.example.accessibility.accelerate;

interface IAccessibilityCallback {

    /** 处理器开始工作 */
    void onStart();

    /** 处理一条业务之前 */
    void before(String result);

    /** 处理一条业务之后 */
    void after(String result);

    /** 处理器停止工作 */
    void onStop();

}
