# apkdiff
比较两个或者多个apk文件差异工具，例如工具可以列出两个apk中哪些文件不同，哪些文件是另外一个apk中没有的，哪些文件是该apk中独有的。

# 用法
`java -jar apkdiff.jar base_app.apk other_app.apk`  
or  
`java -jar apkdiff.jar base_app.apk dir_of_other_apps`  
  
  
output:  
>多余：  
……   
other_app.apk中有的文件但是base_app.apk没有的文件   
……  
缺失：  
……  
other_app.apk中没有但是base_app.apk中存在的文件  
……    
差异：  
……  
other_app.apk和base_app.apk中都存在，但是内容不同的文件  
……  

# 原理
签名后的apk压缩文件内，的`META-INF/`目录下的`MANIFEST.MF`文件记录了apk中每个文件的哈希值，所以可以通过对比两个apk中的`MANIFEST.MF`文件得到两个apk中文件的差异情况，哪些文件是另一个apk中独有的、哪些文件是另一个apk中没有的以及哪些文件两个apk中都存在但内容不同。  

# 应用场景  
Android开发过程中可以使用Android Studio生成debug版和release版安装包。一般来说，测试结束后会使用打包工具打包出渠道包，理想情况下，最终的渠道包应该跟最后测试的一个release版安装包一样，但是实际开发中，难免打包过程中依赖库忘记更新或者依赖版本不对，而这种情况再最后测试渠道包时很难发现。为了应付这种情况，所以开发了这个`apkdiff`工具，用来比较apk差异。
