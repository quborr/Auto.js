package com.stardust.autojs.script;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.stardust.autojs.rhino.TokenStream;
import com.stardust.util.MapBuilder;

import org.mozilla.javascript.Token;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Created by Stardust on 2017/8/2.
 */

public abstract class JavaScriptSource extends ScriptSource {

    public static final String ENGINE = "com.stardust.autojs.script.JavaScriptSource.Engine";

    public static final String EXECUTION_MODE_UI_PREFIX = "\"ui\";";

    public static final int EXECUTION_MODE_NORMAL = 0;
    public static final int EXECUTION_MODE_UI = 0x00000001;
    public static final int EXECUTION_MODE_AUTO = 0x00000002;

    private static final Map<String, Integer> EXECUTION_MODES = new MapBuilder<String, Integer>()
            .put("ui", EXECUTION_MODE_UI)
            .put("auto", EXECUTION_MODE_AUTO)
            .build();
    private static final int EXECUTION_MODE_STRING_MAX_LENGTH = 7;

    private int mExecutionMode = -1;

    public JavaScriptSource(String name) {
        super(name);
    }

    @NonNull
    public abstract String getScript();

    @Nullable
    public abstract Reader getScriptReader();

    @NonNull
    public Reader getNonNullScriptReader() {
        Reader reader = getScriptReader();
        if (reader == null) {
            return new StringReader(getScript());
        }
        return reader;
    }

    public String toString() {
        return getName() + ".js";
    }


    public int getExecutionMode() {
        if (mExecutionMode == -1) {
            mExecutionMode = parseExecutionMode(getScript());
        }
        return mExecutionMode;
    }

    private int parseExecutionMode(String script) {
        if (script == null || script.length() == 0)
            return EXECUTION_MODE_NORMAL;
        if(script.charAt(0) == '"'){
            int i = script.lastIndexOf("\";", EXECUTION_MODE_STRING_MAX_LENGTH + 2);
            if (i >= 0){
                String modeString = script.substring(1, i);
                return parseExecutionMode(modeString.split(" "));
            }
        }
        return EXECUTION_MODE_NORMAL;

    }

    private int parseExecutionMode(String[] modeStrings) {
        int mode = 0;
        for (String modeString : modeStrings) {
            Integer i = EXECUTION_MODES.get(modeString);
            if (i != null) {
                mode |= i;
            }
        }
        return mode;
    }

    @Override
    public String getEngineName() {
        return ENGINE;
    }


}
