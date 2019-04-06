package VectorTools;

import java.util.HashMap;

public class TagConverter {

    public static final int ANDROID_VECTOR_IMAGE = 0;

    public static final int SVG_IMAGE = 1;

    public static final String NOT_FOUND = "not_found";

    private static HashMap<String, String> svgToAndroid = new HashMap<>();
    private static HashMap<String, String> androidToSvg = new HashMap<>();

    static {
        svgToAndroid.put("svg", "android");
        svgToAndroid.put("path", "path");
        svgToAndroid.put("fill", "android:fillColor");
        svgToAndroid.put("d", "android:pathData");
        svgToAndroid.put("fill-rule", "android:fillType");
//        svgToAndroid.put("path", "path");
//        svgToAndroid.put("path", "path");
    }

    static {
        for (String key : svgToAndroid.keySet()) {
            androidToSvg.put(svgToAndroid.get(key), key);
        }
    }

    public static String getConvertedTag(int imageType, String tag){
        if (imageType == ANDROID_VECTOR_IMAGE){
            return androidToSvg.getOrDefault(tag, NOT_FOUND);
        } else {
            return svgToAndroid.getOrDefault(tag, NOT_FOUND);
        }
    }


    public static String convertValueForTag(int imageType, String tag, String value){
        if (imageType == ANDROID_VECTOR_IMAGE) {
            if (tag.equals("android:fillType"))
                return value.toLowerCase();
        } else {
            if (tag.equals("fill"))
                switch (value){
                    default:
                    case "nonzero":
                        return "nonZero";
                    case "evenodd":
                        return "evenOdd";
                }
        }
        return value;
    }
}
