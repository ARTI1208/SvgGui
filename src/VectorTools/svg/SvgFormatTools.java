package VectorTools.svg;

import VectorTools.BaseVectorTools;
import VectorTools.android.AndroidFormatTools;
import java.util.ArrayList;
import static VectorTools.Utils.StringUtils;

public class SvgFormatTools extends BaseVectorTools {

    public static final String DEFAULT_COLOR = "#ff000000";
    public static final String OPENING = "<svg height=\"{real_height}\" width=\"{real_width}\" " +
            "viewBox=\"{view_horizontal_translation} {view_vertical_translation} {view_height} {view_width}\" " +
            "xmlns=\"http://www.w3.org/2000/svg\">\n";
    public static final String PATH = "\t<path fill=\"{path_color}\" d=\"{path}\" />\n";
    public static final String ENDING = "</svg>";
    private static final String PATH_TAG = " d";

    public static ArrayList<String> getTags(String line) {
        ArrayList<String> currentLineTagList = new ArrayList<>();
        line = line.substring(1);
        while (line.length() != 0) {
            String tag = "";
            if (line.contains(" ") && line.contains("\"")) {
                int ind = Math.min(line.indexOf(" "), line.indexOf("\""));
                tag = line.substring(0, ind);
                line = line.substring(ind);
                if (line.charAt(0) == ' ')
                    line = line.substring(1);
                else
                    line = line.substring(StringUtils.charIndex(line, '\"', 2) + 2);
            } else if (line.contains(" ")) {
                tag = line.substring(0, line.indexOf(" "));
                line = line.substring(line.indexOf(" ") + 1);
            } else if (line.contains("\"")) {
                tag = line.substring(0, line.indexOf("\""));
                line = line.substring(StringUtils.charIndex(line, '\"', 2) + 2);
            }

//            PrintUtils.writeln("li - " + line);
            if (tag.endsWith("="))
                currentLineTagList.add(StringUtils.removeChar(tag, '='));

            if (line.equals("/>") || line.equals("/"))
                line = "";
        }
        return currentLineTagList;
    }

    public static String convertToAndroidVector(String input) {
        String realHeight = input.substring(input.indexOf("height=\"") + "height=\"".length(),
                StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("height=\"") + "height=\"".length()));
        String realWidth = input.substring(input.indexOf("width=\"") + "width=\"".length(),
                StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("width=\"") + "width=\"".length()));

        String viewBox = input.substring(input.indexOf("viewBox=\"") + "viewBox=\"".length(),
                StringUtils.charIndexAfterPosition(input, '\"', input.indexOf("viewBox=\"") + "viewBox=\"".length()));

        String viewHeight = getViewBoxNumbers(viewBox)[2];
        String viewWidth = getViewBoxNumbers(viewBox)[3];
        StringBuilder builder = new StringBuilder(AndroidFormatTools.OPENING.
                replace("{real_height}", realHeight).
                replace("{real_width}", realWidth).
                replace("{view_height}", viewHeight).
                replace("{view_width}", viewWidth));

        String[] list = input.split(">");

        for (String s : list) {
            if (s.contains(PATH_TAG)) {
                String path = s.substring(s.indexOf(" d=\"") + " d=\"".length(),
                        StringUtils.charIndexAfterPosition(s, '\"', s.indexOf(" d=\"") + " d=\"".length()));

                builder.append(AndroidFormatTools.PATH.
                        replace("{path_color}",
                                s.contains("fill=")
                                        ? s.substring(s.indexOf(" fill=\"") + " fill=\"".length(),
                                        StringUtils.charIndexAfterPosition(s, '\"', s.indexOf(" fill=\"") +
                                                " fill=\"".length()))
                                        : AndroidFormatTools.DEFAULT_COLOR).
                        replace("{path}", path));
            }
        }

        builder.append(AndroidFormatTools.ENDING);

        return builder.toString();
    }

    private static String[] getViewBoxNumbers(String s) {
        return s.split(" ");
    }

    public static String makeAbsolute(String input) {
        return makeAbsolute(input, PATH_TAG);
    }

    public static String makeRelative(String input) {
        return makeRelative(input, PATH_TAG);
    }

    public static ArrayList<Double> getSizeInfo(String svg) {
        return getSizeInfo(getPaths(svg));
    }

    private static ArrayList<String> getPaths(String svg) {
        String[] lines = svg.split(">");
        ArrayList<String> paths = new ArrayList<>();
        for (String str : lines) {
            if (str.contains(PATH_TAG)) {
                int start = str.indexOf(PATH_TAG) + (PATH_TAG + "=\"").length();
                String path = str.substring(start,
                        StringUtils.charIndexAfterPosition(str, '\"', start));
                paths.add(path);
            }
        }
        return paths;
    }

}
