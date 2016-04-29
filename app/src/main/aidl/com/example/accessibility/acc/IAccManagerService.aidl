// IAccManagerService.aidl
package com.example.accessibility.acc;

import com.example.accessibility.acc.IAccProgressListener;

// Declare any non-default types here with import statements

interface IAccManagerService {

    void startAcc(in List<String> targetPkgNames, IAccProgressListener listener);

    void stopAcc();

}
