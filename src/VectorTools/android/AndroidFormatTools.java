package VectorTools.android;

import VectorTools.Utils;
import VectorTools.svg.*;

public class AndroidFormatTools extends SvgFormatTools {

    public static final String DEFAULT_COLOR = "#ff000000";
    public static final String PATH_TAG = " android:pathData";

    public static final String OPENING = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<vector android:height=\"{real_height}\" android:width=\"{real_width}\" " +
            "android:viewportHeight=\"{view_height}\" android:viewportWidth=\"{view_width}\"\n" +
            "\txmlns:android=\"http://schemas.android.com/apk/res/android\">\n";

    public static final String PATH = "\t<path android:fillColor=\"{path_color}\" android:pathData=\"{path}\" />\n";

    public static final String ENDING = "</vector>";

    public static String convertToSVG(String input){
        String realHeight = input.substring(input.indexOf("android:height=\"") + "android:height=\"".length(),
                Utils.StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("android:height=\"") +
                        "android:height=\"".length()));
        String realWidth = input.substring(input.indexOf("android:width=\"") + "android:width=\"".length(),
                Utils.StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("android:width=\"") +
                        "android:width=\"".length()));

        String viewHeight = input.substring(input.indexOf("android:viewportHeight=\"") + "android:viewportHeight=\"".length(),
                Utils.StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("android:viewportHeight=\"") +
                        "android:viewportHeight=\"".length()));
        String viewWidth = input.substring(input.indexOf("android:viewportWidth=\"") + "android:viewportWidth=\"".length(),
                Utils.StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("android:viewportWidth=\"") +
                        "android:viewportWidth=\"".length()));

        StringBuilder builder = new StringBuilder(SvgFormatTools.OPENING.
                replace("{real_height}", realHeight).
                replace("{real_width}", realWidth).
                replace("{view_height}", viewHeight).
                replace("{view_width}", viewWidth).
                replace("{view_horizontal_translation}", "0").
                replace("{view_vertical_translation}", "0"));

        String[] list = input.split(">");

        for (String s : list) {
            if (s.contains(PATH_TAG)){
                String path = s.substring(s.indexOf("android:pathData=\"") + "android:pathData=\"".length(),
                        Utils.StringUtils.charIndexAfterPosition(s, '\"', s.indexOf("android:pathData=\"") +
                                "android:pathData=\"".length()));

                builder.append(SvgFormatTools.PATH.
                        replace("{path_color}",
                                s.contains("android:fillColor=")
                                ?  s.substring(s.indexOf(" android:fillColor=\"") + " android:fillColor=\"".length(),
                                Utils.StringUtils.charIndexAfterPosition(s, '\"',
                                        s.indexOf(" android:fillColor=\"") + " android:fillColor=\"".length()))
                                : AndroidFormatTools.DEFAULT_COLOR).
                        replace("{path}", path));
            }
        }

        builder.append(SvgFormatTools.ENDING);

        return builder.toString();
    }

    public static String makeRelative(String input){
        return SvgFormatTools.makeRelative(input, PATH_TAG);
    }

    public static String makeAbsolute(String input){
        return SvgFormatTools.makeAbsolute(input, PATH_TAG);
    }
}
