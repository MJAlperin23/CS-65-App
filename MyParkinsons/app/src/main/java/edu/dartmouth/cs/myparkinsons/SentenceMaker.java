package edu.dartmouth.cs.myparkinsons;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by Andrew on 2/26/15.
 */
public class SentenceMaker {


    private TextView text;

    public HashMap<String, ArrayList<GrammarRule>>makeGrammarRule(Context context) {
        HashMap<String, ArrayList<GrammarRule>> grammar = new HashMap<>();

        InputStream is = context.getResources().openRawResource(R.raw.englishfile);
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String sCurrentLine;
        try {
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                if (sCurrentLine.length() == 0 || sCurrentLine.substring(0,1).equals("#") || sCurrentLine.substring(0,1).equals("\n")) {
                    continue;
                } else {
                    sCurrentLine.replace("\n", "");


                    StringTokenizer st = new StringTokenizer(sCurrentLine, "\t");
                    String key = st.nextToken();
                    String listString = st.nextToken();

                    GrammarRule rule = new GrammarRule();
                    rule.current = key;

                    StringTokenizer newST = new StringTokenizer(listString, " ");
                    ArrayList<String> newList = new ArrayList<>();
                    while (newST.hasMoreElements()) {
                        newList.add(newST.nextToken());
                    }

                    if (newList.size() > 1) {
                        rule.others = newList;
                        rule.terminal = null;
                    } else {
                        rule.terminal = newList.get(0);
                        rule.others = null;
                    }

                    if (grammar.containsKey(key)) {
                        ArrayList<GrammarRule> list = grammar.get(key);
                        list.add(rule);

                    } else {
                        ArrayList<GrammarRule> list = new ArrayList<>();
                        list.add(rule);

                        grammar.put(key, list);
                    }

                }
            }

            bufferedReader.close();
            inputStreamReader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grammar;
    }

    public void generateRandomSentence(HashMap<String, ArrayList<GrammarRule>> grammar, TextView text) {
        this.text = text;

        ArrayList<GrammarRule> list = new ArrayList<>();
        getSentence(grammar, "S", list);
        StringBuilder builder = new StringBuilder();
        for (GrammarRule rule : list) {
            builder.append(rule.terminal);
            builder.append(" ");

        }

        text.setText(builder.toString());


        //makeSentence(grammar);
    }

    private void getSentence(HashMap<String, ArrayList<GrammarRule>> grammar, String key, ArrayList<GrammarRule> ruleList) {
        if (!grammar.containsKey(key)) {
            return;
        }

        ArrayList<GrammarRule> nextKeys = grammar.get(key);

        Random rand = new Random();
        int index = Math.abs(rand.nextInt());
        index = index % nextKeys.size();


        GrammarRule rule = nextKeys.get(index);

        if (rule.others != null) {
            for (String newKey : rule.others) {
                getSentence(grammar, newKey, ruleList);
            }
        }

        if (rule.terminal != null) {
            ruleList.add(rule);
        }
    }

//    private void makeSentence(final HashMap<String, ArrayList<GrammarRule>> grammar) {
//        new AsyncTask<HashMap<String, ArrayList<GrammarRule>>, Void, String>() {
//
//            @Override
//            protected String doInBackground(HashMap<String, ArrayList<GrammarRule>>... arg0) {
//                ArrayList<GrammarRule> list = new ArrayList<>();
//                getSentence(arg0[0], "S", list);
//                StringBuilder builder = new StringBuilder();
//                for (GrammarRule rule : list) {
//                    builder.append(rule.terminal);
//                    builder.append(" ");
//
//                }
//
//                return builder.toString();
//            }
//
//            @Override
//            protected void onPostExecute(String res) {
//                text.setText(res);
//            }
//        }.execute(grammar);
//    }

    public class GrammarRule {

        public String current;
        public ArrayList<String> others;
        public String terminal;

        private GrammarRule() {
            this.others = new ArrayList<>();
        }
    }
}


