---
date: 2016-04-06 10:50
status: public
title: uexNFC
---


[TOC]
# 1、简介 [![](http://appcan-download.oss-cn-beijing.aliyuncs.com/%E5%85%AC%E6%B5%8B%2Fgf.png)]() 
NFC插件
uexNFC

##   1.1.说明
NFC插件，扫描NFC卡片信息。

* 目前仅支持返回id和支持协议名
* 目前只支持Android


##   1.2.UI展示
 

 
##  1.3.公告


##  1.4.开源源码
插件测试用例与源码下载:[点击](https://github.com/BadWaka2/uexNFC) 插件中心至插件详情页 (插件测试用例与插件源码已经提供)

##  1.5.使用流程
1. 调用isNFCSupport接口判断设备是否支持NFC功能;
2. 调用isNFCOpen接口判断设备是否打开了NFC功能;
3. 调用startScanNFC尝试开始扫描NFC状态，若返回成功，则将卡片放在设备的感应区，即可接收到cbGetNFCInfo的回调;
4. 若用户中途想取消扫描NFC状态，调用stopScanNFC接口即可停止扫描NFC状态。

#    2、API概览

##  2.1. 方法
 

> ###   isNFCSupport 判断设备是否支持NFC功能

`uexNFC.isNFCSupport()    `

**说明:**

使用插件之前，必须先调用此接口，判断设备是否支持NFC功能；
不支持则不能使用其他功能。
回调 [cbIsNFCSupport](#cbIsNFCSupport 判断设备是否支持NFC功能的回调方法 "cbIsNFCSupport")
 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
 
**平台支持:**

Android2.2+ 
iOS6.0+ 

**版本支持:**

3.0.0+  

**示例:**

```
uexNFC.isNFCSupport();
```

> ###   isNFCOpen 判断设备NFC是否打开

` uexNFC.isNFCOpen()   `

**说明:**

使用插件之前，必须先调用此接口，判断设备是否打开NFC功能；
如未打开则进行提示，让用户在设置里手动打开NFC开关。  
回调 [cbIsNFCOpen](#cbIsNFCOpen 判断设备是否打开NFC功能的回调方法 "cbIsNFCOpen")


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
   

**平台支持:**

Android2.2+ 
iOS6.0+ 

**版本支持:**

3.0.0+  

**示例:**

```
  uexNFC.isNFCOpen();

```

> ###   startScanNFC 开始扫描NFC

` uexNFC.startScanNFC()    `

**说明:**

调用这个接口，若返回成功，则进入开始扫描NFC状态；
将支持NFC的卡片放在设备感应区附近即可接受到cbGetNFCData（得到NFC数据的回调），若成功接受cbGetNFCData的回调，则会自动停止扫描NFC状态；
若想手动停止扫描NFC状态，请调用stopScanNFC接口。
回调 [cbStartScanNFC](#cbStartScanNFC 进入开始扫描NFC状态的回调方法 "cbStartScanNFC")


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
   

**平台支持:**

Android2.2+ 
iOS6.0+ 

**版本支持:**

3.0.0+  

**示例:**

```
  uexNFC.startScanNFC();

```

> ###   stopScanNFC 停止扫描NFC状态

` uexNFC.stopScanNFC()    `

**说明:**

停止扫描NFC状态，提供一个手动的停止开关，当用户取消扫描NFC时须调用改接口。
回调 [cbStopScanNFC](#cbStopScanNFC 停止扫描NFC状态的回调方法 "cbStopScanNFC")


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
   

**平台支持:**

Android2.2+ 
iOS6.0+ 

**版本支持:**

3.0.0+  

**示例:**

```
  uexNFC.stopScanNFC();

```
##2.2.回调方法

> ###cbIsNFCSupport 判断设备是否支持NFC回调

 uexNFC.cbIsNFCSupport(opCode,dataType,data) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number类型 | 必选 | 操作ID,此函数中不起作用,可忽略。  |
| dataType | Number类型 | 必选 | 数据类型详见CONSTANT中Callback方法数据类型     |
| data | String类型 | 必选 | 返回设备是否支持NFC标志，返回1代表设备支持，0代表设备不支持 |


**版本支持:**

   3.0.0
   
   
> ###cbIsNFCOpen 判断NFC是否开启回调

 uexNFC.cbIsNFCOpen(opCode,dataType,data) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number类型 | 必选 | 操作ID,此函数中不起作用,可忽略。  |
| dataType | Number类型 | 必选 | 数据类型详见CONSTANT中Callback方法数据类型     |
| data | String类型 | 必选 | 返回NFC是否开启标志，返回1代表NFC功能已开启，0代表NFC功能未开启 |


**版本支持:**

   3.0.0

   
> ###cbStartScanNFC 开始扫描NFC回调	

 uexNFC.cbStartScanNFC(opCode,dataType,data) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number类型 | 必选 | 操作ID,此函数中不起作用,可忽略。  |
| dataType | Number类型 | 必选 | 数据类型详见CONSTANT中Callback方法数据类型     |
| data | String类型 | 必选 | 返回进入扫描NFC状态成功标志，返回1代表进入扫描NFC状态成功，返回0代表进入扫描NFC状态失败 |


**版本支持:**

   3.0.0
   

> ###cbStopScanNFC 停止扫描NFC回调

 uexNFC.cbStopScanNFC(opCode,dataType,data) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number类型 | 必选 | 操作ID,此函数中不起作用,可忽略。  |
| dataType | Number类型 | 必选 | 数据类型详见CONSTANT中Callback方法数据类型     |
| data | String类型 | 必选 | 返回停止扫描NFC状态是否成功标志，返回1代表停止扫描NFC状态成功，0代表停止扫描NFC状态失败 |


**版本支持:**

   3.0.0
         
> ###cbGetNFCData 得到NFC数据回调

 uexNFC.cbGetNFCData(opCode,dataType,data) 
 
**说明:**
在接受到该回调时会自动停止扫描NFC状态。

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number类型 | 必选 | 操作ID,此函数中不起作用,可忽略。  |
| dataType | Number类型 | 必选 | 数据类型详见CONSTANT中Callback方法数据类型     |
| data | String类型 | 必选 | 返回一个JSON字符串 |

```
  data = "{
  			"uid":"D0453393",				//卡片id，十六进制格式
  			"technologies":"NfcA,MifareClassic,NdefFormatable",			//卡片支持的NFC协议名称，使用逗号区分
		  }";

```
**版本支持:**

   3.0.0   

# 3、更新历史

### iOS

API版本:`无`

最近更新时间:`无`

| 历史发布版本 | 更新内容 |
| ----- | ----- |

### Android

API版本:`uexNFC-3.0.3`

最近更新时间:`2016-04-06`

| 历史发布版本 | 更新内容 |
| ----- | ----- |
| 3.0.3 | 在EUExNFC中直接启用前台调用，优化了开始扫描时前端页面不能响应的问题;增加了stopScanNFC停止扫描NFC接口 |
| 3.0.2 | 使用透明Activity，取消了弹出NFC应用选择下拉菜单 |
| 3.0.1 | 添加了id和支持协议类型 |
| 3.0.0 | uexNFC插件 |

