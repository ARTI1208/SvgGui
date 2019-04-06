package VectorTools;

import VectorTools.android.AndroidFormatTools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BaseVectorTools {

    protected static HashMap<Character, Integer> map = new HashMap<>();

    static {
        map.put('m', 1);
        map.put('l', 1);
        map.put('h', 1);
        map.put('v', 1);
        map.put('c', 3);
        map.put('s', 2);
    }

    public static String makeAbsolute(String input, String tag) {
        if (tag.startsWith(" "))
            tag = tag.trim();
        String search = " " + tag + "=\"";
        String[] lines = input.split("\n");
        StringBuilder builder = new StringBuilder();
        boolean opened = false;
        double maxX = 0;
        double maxY = 0;
        double currentX = 0;
        double currentY = 0;

        double startX = 0;
        double startY = 0;

        double minX = 1000000000;
        double minY = 1000000000;
        int line = 0;
        for (String str : lines) {
            line++;
            if (str.contains(search) || opened) {
                opened = !str.contains("/>");
                int rem = str.indexOf(search) + search.length();
                if (str.contains(search)) {
                    currentX = 0;
                    currentY = 0;
                } else
                    rem = 0;
                StringBuilder done = new StringBuilder(str.substring(0, rem));
                str = str.substring(rem);
                int e = str.indexOf("\"");
                if (!str.contains("\""))
                    e = str.length();
                String ending = str.substring(e);
                str = str.substring(0, e);
                int pos = 0;
                boolean f = false;
                while (!f) {
                    char[] cs = str.toCharArray();
                    if (!((cs[0] >= 'a' && cs[0] <= 'z') || (cs[0] >= 'A' && cs[0] <= 'Z')))
                        str = str.substring(1);
                    else
                        f = true;
                }
                while (str.length() != 0) {
                    char[] cs = str.toCharArray();
                    int end;
                    String w;
                    ArrayList<MyPoint> pts;
                    char c = cs[pos];
                    switch (c) {
                        default:
                            ++pos;
                            continue;
                        case ' ':
                            str = str.trim();
                            break;
                        case 'z':
                        case 'Z':
                            currentX = startX;
                            currentY = startY;
                            done.append("Z");
                            str = str.substring(1);
                            break;
                        case 'm':
                        case 'l':
                        case 'h':
                        case 'v':
                        case 'c':
                        case 's':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            pts = getPoints(w);
                            done.append(Character.toUpperCase(c));
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(c) == 0) {
                                    if (c != 'v') {
                                        currentX += pts.get(i).x;
                                        builderAppend(done, currentX);
                                    }
                                    if (!(c == 'v' || c == 'h'))
                                        done.append(',');
                                    if (c != 'h') {
                                        currentY += pts.get(i).y;
                                        builderAppend(done, currentY);
                                    }

                                    if (c == 'm') {
                                        startX = currentX;
                                        startY = currentY;
                                    }

                                    if (i != pts.size() - 1)
                                        done.append(" ");

                                } else {
                                    double pointerX = pts.get(i).x;
                                    double pointerY = pts.get(i).y;
                                    builderAppend(done, pointerX + currentX).append(',');
                                    builderAppend(done, pointerY + currentY).append(' ');
                                }
                            }
                            str = str.substring(end);
                            break;
                        case 'M':
                        case 'L':
                        case 'H':
                        case 'V':
                        case 'C':
                        case 'S':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            char cl = Character.toLowerCase(c);
                            pts = getPoints(w);
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(cl) == 0) {
                                    if (c != 'V')
                                        currentX = pts.get(i).x;
                                    if (c != 'H')
                                        currentY = pts.get(i).y;

                                    if (c == 'M') {
                                        startX = currentX;
                                        startY = currentY;
                                    }
                                }
                            }
                            done.append(str, 0, end);
                            str = str.substring(end);
                            break;
                    }
                    if (currentX > maxX)
                        maxX = currentX;
                    if (currentY > maxY)
                        maxY = currentY;
                    if (currentX < minX)
                        minX = currentX;
                    if (currentY < minY)
                        minY = currentY;
                }
                builder.append(done).append(ending).append("\n");
            } else {
                builder.append(str);
                if (line != lines.length)
                    builder.append("\n");
            }
        }
        Utils.PrintUtils.writeln("max x = " + maxX + "\nmax y = " + maxY);
        Utils.PrintUtils.writeln("min x = " + minX + "\nmin y = " + minY);
        return builder.toString();
    }

    public static String makeRelative(String input, String tag) {
        String search = tag + "=\"";
        String[] lines = input.split("\n");
        StringBuilder builder = new StringBuilder();
        boolean opened = false;
        double maxX = 0;
        double maxY = 0;
        double currentX = 0;
        double currentY = 0;
        double startX = 0;
        double startY = 0;
        double minX = 1000000000;
        double minY = 1000000000;
        int line = 0;
        for (String str : lines) {
            line++;
            if (str.contains(search) || opened) {
                opened = !str.contains("/>");
                int rem = str.indexOf(search) + search.length();
                if (str.contains(search)) {
                    currentX = 0;
                    currentY = 0;
                } else
                    rem = 0;
                StringBuilder done = new StringBuilder(str.substring(0, rem));
                str = str.substring(rem);
                int e = str.indexOf("\"");
                if (!str.contains("\""))
                    e = str.length();
                String ending = str.substring(e);
                str = str.substring(0, e);
                int pos = 0;
                boolean f = false;
                while (!f) {
                    char[] cs = str.toCharArray();
                    if (!((cs[0] >= 'a' && cs[0] <= 'z') || (cs[0] >= 'A' && cs[0] <= 'Z')))
                        str = str.substring(1);
                    else
                        f = true;
                }
                while (str.length() != 0) {
                    char[] cs = str.toCharArray();
                    int end;
                    double moveX;
                    double moveY;
                    String w;
                    ArrayList<MyPoint> pts;
                    char c = cs[pos];
                    switch (c) {
                        default:
                            ++pos;
                            continue;
                        case ' ':
                            str = str.trim();
                            break;
                        case 'z':
                        case 'Z':
                            currentX = startX;
                            currentY = startY;
                            done.append("z");
                            str = str.substring(1);
                            break;
                        case 'm':
                        case 'l':
                        case 'h':
                        case 'v':
                        case 'c':
                        case 's':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            pts = getPoints(w);
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(c) == 0) {
                                    if (c != 'v')
                                        currentX += pts.get(i).x;
                                    if (c != 'h')
                                        currentY += pts.get(i).y;

                                    if (c == 'm') {
                                        startX = currentX;
                                        startY = currentY;
                                    }

                                }
                            }
                            done.append(str, 0, end);
                            str = str.substring(end);
                            break;
                        case 'M':
                        case 'L':
                        case 'H':
                        case 'V':
                        case 'C':
                        case 'S':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            pts = getPoints(w);
                            char cl = Character.toLowerCase(c);
                            done.append(cl);
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(cl) == 0) {
                                    if (c != 'V') {
                                        moveX = pts.get(i).x - currentX;
                                        currentX += moveX;
                                        builderAppend(done, moveX);
                                    }
                                    if (c != 'H' && c != 'V')
                                        done.append(',');
                                    if (c != 'H') {
                                        moveY = pts.get(i).y - currentY;
                                        currentY += moveY;
                                        builderAppend(done, moveY);
                                    }
                                    if (i != pts.size() - 1)
                                        done.append(" ");


                                    if (c == 'M') {
                                        startX = currentX;
                                        startY = currentY;
                                    }


                                } else {
                                    double pointerX = pts.get(i).x - currentX;
                                    double pointerY = pts.get(i).y - currentY;
                                    builderAppend(done, pointerX).append(',');
                                    builderAppend(done, pointerY).append(' ');
                                }
                            }
                            str = str.substring(end);
                            break;
                    }
                    if (currentX > maxX)
                        maxX = currentX;
                    if (currentY > maxY)
                        maxY = currentY;
                    if (currentX < minX)
                        minX = currentX;
                    if (currentY < minY)
                        minY = currentY;
                }
                builder.append(done).append(ending).append("\n");
            } else {
                builder.append(str);
                if (line != lines.length)
                    builder.append("\n");
            }
        }
        Utils.PrintUtils.writeln("max x = " + maxX + "\nmax y = " + maxY);
        Utils.PrintUtils.writeln("min x = " + minX + "\nmin y = " + minY);
        return builder.toString();
    }

    private static int getNumberEnd(String w) {
        int add = 0;
        if (w.charAt(0) == '-') {
            add++;
            w = w.substring(1);
        }
        int ch = 0;
        if (w.contains(" "))
            ch += 1;
        if (w.contains("-"))
            ch += 10;
        if (w.contains(","))
            ch += 100;
        int ret;
        switch (ch) {
            default:
                Utils.PrintUtils.writeln("Can't get end");
                ret = -1;
                break;
            case 0:
                ret = w.length();
                break;
            case 1:
                ret = w.indexOf(" ");
                break;
            case 10:
                ret = w.indexOf("-");
                break;
            case 100:
                ret = w.indexOf(",");
                break;
            case 11:
                ret = Math.min(w.indexOf(" "), w.indexOf("-"));
                break;
            case 101:
                ret = Math.min(w.indexOf(" "), w.indexOf(","));
                break;
            case 110:
                ret = Math.min(w.indexOf("-"), w.indexOf(","));
                break;
            case 111:
                ret = Math.min(Math.min(w.indexOf("-"), w.indexOf(",")), w.indexOf(" "));
                break;
        }
        int pp;
        if ((pp = Utils.StringUtils.charIndex(w, '.', 2)) != -1 && pp < ret)
            return pp + add;
        return ret + add;
    }

    private static double getNextNum(String w, int end) {
        return Double.parseDouble(w.substring(0, end));
    }

    private static ArrayList<MyPoint> getPoints(String w) {
        ArrayList<MyPoint> ps = new ArrayList<>();
        double x = 0;
        double y = 0;
        int end;
        boolean init = false;
        char c = 'm';
        while (w.length() != 0) {
            if ((w.startsWith(",") || w.startsWith(" ")))
                w = w.substring(1).trim();
            if (!init) {
                c = Character.toLowerCase(w.charAt(0));
                w = w.substring(1).trim();
                init = true;
            }
            if (c != 'v') {
                end = getNumberEnd(w);
                x = getNextNum(w, end);
                w = w.substring(end);
                if (w.startsWith(",") || w.startsWith(" "))
                    w = w.substring(1).trim();
            }
            if (c != 'h') {
                end = getNumberEnd(w);
                y = getNextNum(w, end);
                w = w.substring(end);
            }
            switch (c) {
                default:
                    ps.add(new MyPoint(x, y));
                    break;
                case 'h':
                    ps.add(new MyPoint(x, true));
                    break;
                case 'v':
                    ps.add(new MyPoint(y, false));
                    break;
            }
            w = w.trim();
        }
        return ps;
    }

    private static StringBuilder builderAppend(StringBuilder builder, double ap) {
        DecimalFormat f = new DecimalFormat("#.##");
        return builder.append(f.format(ap).replaceAll(",", "."));
    }

    private static int getEnd(char[] cs) {
        return getEnd(String.copyValueOf(cs));
    }

    private static int getEnd(String s) {
        int len = 0;
        if (s.startsWith(" "))
            len = s.length() - (s = s.trim()).length();
        boolean f = false;
        int p = 0;
        for (char c : s.toCharArray()) {
            if ((c >= 'a') && (c <= 'z') || (c >= 'A') && (c <= 'Z')) {
                if (f)
                    return p + len;
                else
                    f = true;
            }
            p++;
        }
        return s.length() + len;
    }

    public static String resize(String input, String tag, double value) {
        if (tag.startsWith(" "))
            tag = tag.trim();
        tag = AndroidFormatTools.PATH_TAG;
        String search = "" + tag + "=\"";
        String[] lines = input.split("\n");
        StringBuilder builder = new StringBuilder();
        boolean opened = false;
        double maxX = 0;
        double maxY = 0;
        double currentX = 0;
        double currentY = 0;

        Utils.PrintUtils.writeln(value);


//        for (String str: lines){
//            PrintUtils.writeln("str - " + str);
//        }

        double startX = 0;
        double startY = 0;

        double minX = 1000000000;
        double minY = 1000000000;
        int line = 0;
        for (String str : lines) {
            line++;
            Utils.PrintUtils.writeln("search - |" + search);
            if (str.contains(search) || opened) {

                Utils.PrintUtils.writeln("str3 - " + str);
                opened = !str.contains("/>");
                int rem = str.indexOf(search) + search.length();
                if (str.contains(search)) {
                    currentX = 0;
                    currentY = 0;
                } else
                    rem = 0;
                StringBuilder done = new StringBuilder(str.substring(0, rem));
                str = str.substring(rem);
                int e = str.indexOf("\"");
                if (!str.contains("\""))
                    e = str.length();
                String ending = str.substring(e);
                str = str.substring(0, e);
                int pos = 0;

                boolean f = false;
                while (!f) {
                    char[] cs = str.toCharArray();
                    if (!((cs[0] >= 'a' && cs[0] <= 'z') || (cs[0] >= 'A' && cs[0] <= 'Z')))
                        str = str.substring(1);
                    else
                        f = true;
                }

                Utils.PrintUtils.writeln("str2 - " + str);

//                PrintUtils.writeln("2 - "+value);

                while (str.length() != 0) {
                    char[] cs = str.toCharArray();
                    int end;
                    String w;
                    ArrayList<MyPoint> pts;
                    char c = cs[pos];
                    switch (c) {
                        default:
                            ++pos;
                            continue;
                        case ' ':
                            str = str.trim();
                            break;
                        case 'z':
                        case 'Z':
                            currentX = startX;
                            currentY = startY;
                            done.append(c);
                            str = str.substring(1);
                            break;
                        case 'm':
                        case 'l':
                        case 'h':
                        case 'v':
                        case 'c':
                        case 's':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            pts = getPoints(w);
                            done.append(c);
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(c) == 0) {
                                    if (c != 'v') {
                                        double moveX = pts.get(i).x * value;
                                        currentX += moveX;
                                        builderAppend(done, moveX);
                                    }
                                    if (!(c == 'v' || c == 'h'))
                                        done.append(',');
                                    if (c != 'h') {
                                        double moveY = pts.get(i).y * value;
                                        currentY += moveY;
                                        builderAppend(done, moveY);
                                    }
                                    if (c == 'm') {
                                        startX = currentX;
                                        startY = currentY;
                                    }
                                    if (i != pts.size() - 1)
                                        done.append(" ");
                                } else {
                                    double pointerX = pts.get(i).x * value;
                                    double pointerY = pts.get(i).y * value;
                                    builderAppend(done, pointerX).append(',');
                                    builderAppend(done, pointerY).append(' ');
                                }
                            }
                            str = str.substring(end);
                            break;
                        case 'M':
                        case 'L':
                        case 'H':
                        case 'V':
                        case 'C':
                        case 'S':
                            end = getEnd(cs);
                            w = str.substring(0, end);
                            pts = getPoints(w);
                            char cl = Character.toLowerCase(c);
                            done.append(c);
                            for (int i = 0; i < pts.size(); i++) {
                                if ((i + 1) % map.get(cl) == 0) {
                                    if (c != 'V') {
                                        double moveX = pts.get(i).x * value;
                                        currentX = moveX;
                                        builderAppend(done, moveX);
                                    }
                                    if (c != 'H' && c != 'V')
                                        done.append(',');
                                    if (c != 'H') {
                                        double moveY = pts.get(i).y * value;
                                        currentY = moveY;
                                        builderAppend(done, moveY);
                                    }
                                    if (i != pts.size() - 1)
                                        done.append(" ");
                                    if (c == 'M') {
                                        startX = currentX;
                                        startY = currentY;
                                    }
                                } else {
                                    double pointerX = pts.get(i).x * value;
                                    double pointerY = pts.get(i).y * value;
                                    builderAppend(done, pointerX).append(',');
                                    builderAppend(done, pointerY).append(' ');
                                }
                            }
                            str = str.substring(end);
                            break;
                    }
                    if (currentX > maxX)
                        maxX = currentX;
                    if (currentY > maxY)
                        maxY = currentY;
                    if (currentX < minX)
                        minX = currentX;
                    if (currentY < minY)
                        minY = currentY;
                }
                builder.append(done).append(ending).append("\n");
            } else {
                builder.append(str);
                if (line != lines.length)
                    builder.append("\n");
            }
        }
        Utils.PrintUtils.writeln("max x = " + maxX + "\nmax y = " + maxY);
        Utils.PrintUtils.writeln("min x = " + minX + "\nmin y = " + minY);
        return builder.toString();
    }

    public static ArrayList<Double> getSizeInfo(ArrayList<String> paths) {
        ArrayList<Double> sizeInfo = new ArrayList<>();
        double startX = 0;
        double startY = 0;
        double currentX;
        double currentY;
        double minX = 1000000000;
        double minY = 1000000000;
        double maxX = 0;
        double maxY = 0;
        int line = 0;
        for (String str : paths) {
            currentX = 0;
            currentY = 0;
            line++;
            int pos = 0;
            while (str.length() != 0) {
                char[] cs = str.toCharArray();
                int end;
                String currentCommand;
                ArrayList<MyPoint> pts;
                char c = cs[pos];
                switch (c) {
                    default:
                        ++pos;
                        continue;
                    case '\n':
                    case ' ':
                        str = str.substring(1);
                        break;
                    case 'z':
                    case 'Z':
                        currentX = startX;
                        currentY = startY;
                        str = str.substring(1);
                        break;
                    case 'm':
                    case 'l':
                    case 'h':
                    case 'v':
                    case 'c':
                    case 's':
                        end = getEnd(cs);
                        currentCommand = str.substring(0, end);
                        pts = getPoints(currentCommand);
                        for (int i = 0; i < pts.size(); i++) {
                            if ((i + 1) % map.get(c) == 0) {
                                if (c != 'v')
                                    currentX += pts.get(i).x;
                                if (c != 'h')
                                    currentY += pts.get(i).y;
                                if (currentX > maxX)
                                    maxX = currentX;
                                if (currentY > maxY)
                                    maxY = currentY;
                                if (currentX < minX)
                                    minX = currentX;
                                if (currentY < minY)
                                    minY = currentY;
                                if (c == 'm') {
                                    startX = currentX;
                                    startY = currentY;
                                }
                            }
                        }
                        str = str.substring(end);
                        break;
                    case 'M':
                    case 'L':
                    case 'H':
                    case 'V':
                    case 'C':
                    case 'S':
                        end = getEnd(cs);
                        currentCommand = str.substring(0, end);
                        pts = getPoints(currentCommand);
                        char cl = Character.toLowerCase(c);
                        for (int i = 0; i < pts.size(); i++) {
                            if ((i + 1) % map.get(cl) == 0) {
                                if (c != 'V')
                                    currentX = pts.get(i).x;
                                if (c != 'H')
                                    currentY = pts.get(i).y;
                                if (currentX > maxX)
                                    maxX = currentX;
                                if (currentY > maxY)
                                    maxY = currentY;
                                if (currentX < minX)
                                    minX = currentX;
                                if (currentY < minY)
                                    minY = currentY;
                                if (c == 'M') {
                                    startX = currentX;
                                    startY = currentY;
                                }
                            }
                        }
                        str = str.substring(end);
                        break;
                }
            }



        }
        sizeInfo.add(maxX);
        sizeInfo.add(maxY);
        sizeInfo.add(minX);
        sizeInfo.add(minY);
        return sizeInfo;
    }

    private static ArrayList<String> getPaths(String svg){
        return new ArrayList<>(Arrays.asList(svg.split("\n")));
    }

}
