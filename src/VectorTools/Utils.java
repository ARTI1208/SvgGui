package VectorTools;

public class Utils {

    public static class PrintUtils{
        public static void writeSp(Object o) {
            System.out.print(o + " ");
        }

        public static void write(Object o) {
            System.out.print(o);
        }

        public static void writeln(Object o) {
            System.out.println(o);
        }
    }

    public static class StringUtils{

        public static int charIndexAfterPosition(String s, char c, int pos){
            String w = s.substring(pos + 1);
            return w.indexOf(c) + pos + 1;
        }

        public static String removeChar2(String string, char c){
            StringBuilder builder = new StringBuilder(string);
            while (builder.indexOf(Character.toString(c)) != -1){
                builder.deleteCharAt(builder.indexOf(Character.toString(c)));
            }
            return builder.toString();
        }

        public static String removeChar(String string, char c){
            return string.replaceAll(Character.toString(c), "");
        }

        public static int charCount(String s, char f) {
            int nums = 0;
            char[] cs = s.toCharArray();
            for (char c : cs) {
                if (c == f)
                    nums++;
            }
            return nums;
        }

        public static int charIndex(String s, char f, int number) {
            if (number == 1)
                return s.indexOf(f);
            if (number < 1)
                return -1;
            int nums = 0;
            char[] cs = s.toCharArray();
            for (int i = 0; i < cs.length; i++) {
                char c = cs[i];
                if (c == f)
                    if (nums + 1 == number)
                        return i;
                    else
                        nums++;

            }
            return -1;
        }

        public static boolean containsNumberOfChar(String s, char f, int num) {
            int nums = 0;
            char[] cs = s.toCharArray();
            for (char c : cs) {
                if (c == f)
                    if (nums + 1 == num)
                        return true;
                    else
                        nums++;
            }
            return false;
        }
    }
}
