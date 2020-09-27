#!/bin/sh
# ****************************************************
# update by martin 2017 11 09
# ****************************************************

# 函数定义，检测执行结果
function checkResult() {
   result=$?
   echo "result : $result"
   if [ $result -eq 0 ];then
      echo "checkResult: execCommand succ"
   else
    echo "checkResult: execCommand failed"
    exit $result
   fi
}

# 函数定义，删除当前目录及其子目录下的空文件夹
deleteempty() {
  find ${1:-.} -mindepth 1 -maxdepth 1 -type d | while read -r dir
  do
    if [[ -z "$(find "$dir" -mindepth 1 -type f)" ]] >/dev/null
    then
      echo "$dir"
      rm -rf ${dir} 2>&- && echo "Empty, Deleted!" || echo "Delete error"
    fi
    if [ -d ${dir} ]
    then
      deleteempty "$dir"
    fi
  done
}

# 检查输入参数
CHECK_PARAMS(){
  echo "params:"$PARAMS1
  if [ "$PARAMS1"x = ""x ];then
    echo "***********************************************"
    echo "  Please Enter a Para for build.sh :"
    echo "    '/bin/bash ./build.sh sdk' for build sdk project"
    echo "    '/bin/bash ./build.sh patch' for build sdk patch"
    echo "***********************************************"
    exit;
  fi
}

# 设置相关变量
SET_VARIABLE(){
    if [ "$ANDROID_HOME"x = ""x ];then
    	echo 'error: ANDROID_HOME 环境变量未设置 ！！！'
    	exit
    fi

    MODEL='release'
    if [ "$PARAMS2"x = "debug"x ];then
      MODEL='debug'
    fi

    echo 'BUILD MODEL:'$MODEL
    echo 'SDK_NAME:'$SDK_NAME
    echo 'ANDROID_HOME:'$ANDROID_HOME
    echo 'LOCAL_PATH:'$LOCAL_PATH
}

# 编译 sdk
BUILD_SDK(){
    echo 'start to build sdk ...'
    cd $LOCAL_PATH && ./gradlew clean -b $SDK_NAME/build.gradle
    cd $LOCAL_PATH && ./gradlew build -b $SDK_NAME/build.gradle
    checkResult
}

# 对 SDK 进行插桩操作
BUILD_HOTFIX_SDK(){
    cd $LOCAL_PATH
    if [ -d "./$TEMP" ]; then
      rm -rf $localPath/$TEMP
    fi
    mkdir -p $LOCAL_PATH/$TEMP/jar
    #解压 sdk 的 jar 到指定目录
    if [ "$MODEL"x = "release"x ];then
      cd $LOCAL_PATH/$SDK_NAME/build/intermediates/bundles
      if [ ! -d "./$MODEL" ];then
        MODEL='default'
      fi
    fi
    unzip -oqbC $LOCAL_PATH/$SDK_NAME/build/intermediates/bundles/$MODEL/classes.jar -d $LOCAL_PATH/$TEMP/jar/

    #对 SDK 代码进行插桩
    cd $LOCAL_PATH && ./gradlew processJarAndGetJarHash
    checkResult
}

# 处理插桩后的 sdk
HANDLE_HOTFIX_SDK(){
    cd $LOCAL_PATH/$TEMP/jar
    rm -rf assets
    rm -rf publicsuffixes.gz
    jar cvf $SDK_JAR_NAME *
    cd $LOCAL_PATH
    if [ ! -d $OUTPUT_DIR ];then
      mkdir $OUTPUT_DIR
    fi
    cd $LOCAL_PATH/$OUTPUT_DIR
    if [ ! -d $SDK_DIR ];then
      mkdir $SDK_DIR
    fi
    #拷贝热更 jar 到指定输出目录
    cp $LOCAL_PATH/$TEMP/jar/$SDK_JAR_NAME $LOCAL_PATH/$OUTPUT_DIR/$SDK_DIR
    #保存此次 sdk 的 hash 文件
    if [ ! -d "$LOCAL_PATH/libs/hash" ]; then
    	mkdir -p $LOCAL_PATH/libs/hash
    fi
    cd $LOCAL_PATH && cp $LOCAL_PATH/$TEMP/*_hash.txt $LOCAL_PATH/libs/hash/
    checkResult

    #清除临时目录
    rm -rf $LOCAL_PATH/$TEMP

    echo 'Build hotfix 【 sdk 】 succ, Well Done !!!'
}

#生成 SDK 的补丁
BUILD_SDK_PATCH(){
    echo 'start to build sdk patch ...'
    cd $LOCAL_PATH && ./gradlew buildPatch
    checkResult
    #删除所有空目录
    cd $LOCAL_PATH/$TEMP/jar && deleteempty
    #将所有需要变更的文件重新打包成 jar
    cd $LOCAL_PATH/$TEMP/jar && jar cvf $SDK_PATCH_JAR_NAME *

    if [ ! -d "$LOCAL_PATH/$OUTPUT_DIR/$PATCH_DIR" ];then
      mkdir -p $LOCAL_PATH/$OUTPUT_DIR/$PATCH_DIR
    fi

    #使用build-tools 将 jar生成为 dex
    ANDROID_LATEST_BUILD_TOOLS=$(ls -r ${ANDROID_HOME}/build-tools|head -1)
    $ANDROID_HOME/build-tools/$ANDROID_LATEST_BUILD_TOOLS/dx --dex --output=$LOCAL_PATH/$OUTPUT_DIR/$PATCH_DIR/$SDK_PATCH_JAR_NAME $LOCAL_PATH/$TEMP/jar/$SDK_PATCH_JAR_NAME

    #压缩补丁包成一个 zip ，并删除补丁 jar 包
    cd $LOCAL_PATH/$OUTPUT_DIR/$PATCH_DIR && zip patch.zip $SDK_PATCH_JAR_NAME && rm -f $SDK_PATCH_JAR_NAME

    #清除临时目录
    rm -rf $LOCAL_PATH/$TEMP
    echo 'Build hotfix 【 sdk patch 】 succ, Well Done !!!'
}

PARAMS1=$1
PARAMS2=$2
MODEL=''
SDK_NAME='GMSDK'
LOCAL_PATH=`pwd`
TEMP="temp"
SDK_JAR_NAME='sdk_hotfix.jar'
SDK_PATCH_JAR_NAME='bassdk_patch_dex.jar'
OUTPUT_DIR='output'
SDK_DIR='sdk_hotfix'
PATCH_DIR='patch'

MAIN(){
   #检查输入参数
   CHECK_PARAMS
   #设置相关变量
   SET_VARIABLE
   #编译 sdk
   BUILD_SDK
   #进行 sdk 插桩操作
   BUILD_HOTFIX_SDK
   #判断操作，是生成 sdk 还是生成补丁
   if [ "$PARAMS1"x = "sdk"x ];then
    HANDLE_HOTFIX_SDK
  elif [ "$PARAMS1"x = "patch"x ];then
    BUILD_SDK_PATCH
   else
     echo 'ERROR: wrong params:' $PARAMS1 '  it should be sdk or patch'
   fi
}

MAIN
