import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class LanguageRecognizer {

    public static class LRMapper extends Mapper<Object, Text, Text, IntWritable>{
        private final static IntWritable one = new IntWritable(1);

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            int indexSentence = 0;
            String[] sentences = value.toString().split("\\r?\\n");

            String nl = "Dutch";
            String en = "English";

            while (indexSentence < sentences.length) {

                String[] words = sentences[indexSentence].split("\\s+");
                int dutchCount = 0, englishCount = 0;

                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].replaceAll("[\\u002C\\u002E\\u201C\\u0022]", "") ;

                    for (int j = 0;j < words[i].length() -1; j++){
                        String combi = (words[i].charAt(j) + "" + words[i].charAt((j + 1 ))).toLowerCase();

                        dutchCount += determineNL(combi);
                        englishCount += determioneEN(combi);
                    }
                }

                if(dutchCount > englishCount){
                    context.write(new Text("Dutch"), one);
                }else if(englishCount > dutchCount){
                    context.write(new Text("English"), one);
                }else{
                    context.write(new Text("Unknown"), one);
                }
            }
        }
    }

    public static class LRReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
          int sum = 0;
          for (IntWritable val : values) {
            sum += val.get();
          }
          result.set(sum);
          context.write(key, result);
        }
    }

    static int determineNL(String combi) {
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

    static int determioneEN(String combi) {
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

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "language count");
        job.setJarByClass(LanguageRecognizer.class);
        job.setMapperClass(LRMapper.class);
        job.setCombinerClass(LRReducer.class);
        job.setReducerClass(LRReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
