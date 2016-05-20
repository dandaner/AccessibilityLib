package com.example.accessibility.accelerate;

import com.example.accessibility.accelerate.IAccessibilityCallback;


interface IAccessibility {

    void open(IAccessibilityCallback callback);

    void start(in int type, in List<String> params, IAccessibilityCallback callback);

    void stop();

}
