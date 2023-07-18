import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        File file;

        file = new File("C:\\Users\\Michel\\IdeaProjects\\LanguageRecognizer\\src\\sentences.nl-en.txt");                       //import work pc
        //file = new File("C:\\Users\\Michel Kastelein\\Documents\\School\\HBO\\Jaar 4\\LanguageRecognizer\\src\\sentences.nl-en.txt");  //import home pc


        //what all languages per line should be (see sentences.nl-enCHECKED.txt)
        String[] languages = {
                "en", "nl", "nl", "nl", "en", "en", "en", "en", "en", "nl", "en", "en", "nl", "en", "nl", "en", "en", "en", "en", "nl", "en", "nl", "en", "en",
                "en", "en", "en", "en", "nl", "nl", "nl", "en", "en", "en", "en", "nl", "en", "en", "en", "en", "en", "nl", "nl", "en", "nl", "en", "nl", "nl",
                "nl", "en", "nl", "en", "en", "nl", "en", "en", "nl", "nl", "en", "en", "en", "--", "en", "en", "en", "en", "nl", "nl", "en", "nl", "en", "nl",
                "en", "en", "nl", "en", "en", "nl", "nl", "nl", "en", "en", "nl", "en", "nl", "nl", "en", "nl", "en", "en", "en", "nl", "nl", "en", "nl", "en",
                "nl", "en", "en", "nl", "en", "nl", "nl", "en", "en", "en", "nl", "en", "nl", "nl", "en", "en", "nl", "en", "nl", "en", "nl", "nl", "en", "en",
                "en", "en", "en", "en", "en", "nl", "nl", "en", "en", "nl", "en", "en", "en", "en", "en", "nl", "en", "en", "nl", "nl", "en", "en", "nl", "en",
                "en", "en", "en", "nl", "nl", "en", "nl", "en", "en", "en", "en", "en", "en", "en", "en", "nl", "nl", "nl", "en", "en", "en", "nl", "en", "en",
                "en", "en", "en", "nl", "en", "nl", "nl", "nl", "en", "en", "en", "en", "nl", "en", "en", "en", "nl", "nl", "nl", "en", "nl", "nl", "nl", "en"};

        BufferedReader br = new BufferedReader(new FileReader(file)); //import file
        String sentence;
        int dutchLines = 0, englishLines = 0, unknown = 0, wrong = 0, iterator = 0;

        while ((sentence = br.readLine()) != null) {            //split per line

            String[] words = sentence.split("\\s+");      //split per sentence

            int dutchCount = 0, englishCount = 0;

            for (int i = 0; i < words.length; i++) {            //loop per letter
                words[i] = words[i].replaceAll("[\\u002C\\u002E\\u201C\\u0022]", ""); //remove dots, commas etc.

                for (int j = 0;j < words[i].length() - 1; j++){     //grab first 2 letters

                    String combi = (words[i].charAt(j) + "" + words[i].charAt((j + 1 ))).toLowerCase();

                    dutchCount += determineNL(combi);               //determine Dutch percentage
                    englishCount += determioneEN(combi);            //determine English percentage
                }
            }

            if(!((englishCount > dutchCount) && (java.util.Objects.equals(languages[iterator], "en")) || ((englishCount < dutchCount) && (java.util.Objects.equals(languages[iterator], "nl"))))){
                System.out.println("Should be:  " + languages[iterator] + " actually is " + englishCount +  " / " + dutchCount + " --- " + sentence);
                wrong++;
            } //print all the faulty lines (for improving algorithm)

            if (dutchCount > englishCount){
                dutchLines++;                       //if more Dutch per sentence then sentence is Dutch
            }else if (englishCount > dutchCount){
                englishLines++;                     //if more English per sentence then sentence is English
            }else{
                unknown++;                          //if equal Dutch and English per sentence then sentence is Unknown
            }

            iterator++;
        }

//        determining amount in text as it should be (output is 73 Dutch / 118 English / 1 unknown)
//
//        int testNL = 0, testEN = 0;
//        for (String lang: languages) {
//            if (lang.equals("nl")) {
//                testNL++;
//            }else if (lang.equals("en")) {
//                testEN++;
//            }
//        }
//        System.out.println("Talen NL: " + testNL);
//        System.out.println("Talen EN: " + testEN);

        System.out.println(wrong + " verkeerd bepaalde talen");
//        (19 in de 192 zijn fout bepaald -> 9.85% is fout (onder de 10%))

        //Output as in hadoop:
        System.out.println("Dutch " + dutchLines );
        System.out.println("English " + englishLines );
        System.out.println("Unknown " + unknown );


    }
    static int determineNL(String combi) {      //method for calculating is a combi is likely to be Dutch
        String[][] langPercentage = {{"en", "75"}, {"er", "95"}, {"de", "95"}, {"ij", "70"}, {"ge", "95"}, {"te", "95"}, {"ee", "95"}, {"el", "85"},
                {"ui", "95"}, {"ei", "95"}, {"aa", "85"}, {"oe", "95"}, {"ou", "95"}, {"ie", "85"}, {"oo", "75"}, {"th", "5"}, {"he", "75"}, {"an", "85"},
                {"re", "75"}, {"nd", "75"}, {"at", "5"}, {"on", "75"}, {"nt", "75"}, {"in", "75"}, {"eu", "95"}, {"ha", "75"}, {"ov", "85"}, {"vo", "85"},
                {"wo", "75"}, {"ro", "75"}, {"op", "85"}, {"pe", "85"}, {"es", "85"}, {"se", "85"}, {"nw", "85"}, {"or", "85"}, {"uy", "5"}, {"pr", "75"},
                {"ic", "85"}, {"di", "85"}, {"ti", "85"}, {"zo", "85"}, {"we", "85"}, {"nh", "75"}, {"st", "85"}, {"ev", "85"}, {"sm", "25"}};

        for (String[] combiNL: langPercentage) {
            if (combi.equals(combiNL[0])) {
                return Integer.parseInt(combiNL[1]);
            }
        }
        return 0;
    }

    static int determioneEN(String combi) {     //method for calculating is a combi is likely to be English
        String[][] langPercentage = {{"en", "95"}, {"er", "95"}, {"de", "95"}, {"ij", "5"}, {"ge", "10"}, {"te", "85"}, {"ee", "95"}, {"el", "95"},
                {"ui", "5"},  {"ei", "85"}, {"aa", "5"}, {"oe", "5"}, {"ou", "85"}, {"ie", "85"}, {"oo", "85"}, {"th", "95"}, {"he", "85"}, {"an", "85"},
                {"re", "85"}, {"nd", "95"}, {"at", "85"}, {"on", "85"}, {"nt", "85"}, {"in", "95"}, {"eu", "5"}, {"ha", "85"}, {"ov", "15"}, {"vo", "15"},
                {"wo", "15"}, {"ro", "75"}, {"op", "85"}, {"pe", "85"}, {"es", "85"}, {"se", "85"}, {"nw", "5"}, {"or", "95"}, {"uy", "5"}, {"pr", "75"},
                {"ic", "85"}, {"di", "85"}, {"ti", "85"}, {"zo", "15"}, {"we", "85"}, {"nh", "15"}, {"st", "95"}, {"ev", "25"}, {"sm", "75"}};

        for (String[] combiEN: langPercentage) {
            if (combi.equals(combiEN[0])) {
                return Integer.parseInt(combiEN[1]);
            }
        }
        return 0;
    }
}