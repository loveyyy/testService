// INoticeServiceAIDL.aidl
package com.example.test;

// Declare any non-default types here with import statements

interface INoticeServiceAIDL {

      /** 当其他服务已经绑定时调起 */
       void onFinishBind();
}
