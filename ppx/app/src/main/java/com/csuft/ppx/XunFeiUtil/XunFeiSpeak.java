package com.csuft.ppx.XunFeiUtil;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by hwm on 2017/5/9.
 */

//讯飞语音说话
public class XunFeiSpeak {

    private  Context mcontext;
    //单例模式
    private static  volatile  XunFeiSpeak xunFeiSpeak;
    //讯飞语音对象
    private  SpeechSynthesizer mTts=null;

    //讯飞语音的监听器
    private SynthesizerListener mSynListener=new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {}
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {}
        //暂停播放
        public void onSpeakPaused() {}
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
    };

    //私有化构造函数
    private  XunFeiSpeak(Context context){
        mcontext=context;
        init();
    }

    //外界调用的初始化函数
    public static XunFeiSpeak getIance(Context context){
        if(xunFeiSpeak==null){
            xunFeiSpeak=new XunFeiSpeak(context);

        }

        return xunFeiSpeak;
    }

    //外界调用的说话函数
    /**
     *
     * @param text 为要说出的文本
     */
    public void Speak(String text){
        mTts.startSpeaking(text,mSynListener);
    }

    //讯飞语音的初始化
    private  void init(){
        SpeechUtility.createUtility(mcontext, SpeechConstant.APPID+"=59074853");
        //创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts=SpeechSynthesizer.createSynthesizer(mcontext, new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        //设置语速
        mTts.setParameter(SpeechConstant.SPEED,"50");
        //设置音量
        mTts.setParameter(SpeechConstant.VOLUME,"80");
        //设置云端
        mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,"./sdcard/iflytek.pcm");

    }

}
