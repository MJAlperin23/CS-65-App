package edu.dartmouth.cs.myparkinsons;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by Andrew on 2/26/15.
 */
public class SentenceMaker {



    public String generateRandomSentence(Context context) {

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
                    //System.out.println(sCurrentLine);
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

                        //grammar.put(key, list);
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

        makeSentence(grammar);

        return "";
    }

    private void getSentence(HashMap<String, ArrayList<GrammarRule>> grammar, String key, ArrayList<GrammarRule> ruleList) {
        if (!grammar.containsKey(key)) {
            System.out.print(key);
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

    private void makeSentence(final HashMap<String, ArrayList<GrammarRule>> grammar) {
        new AsyncTask<HashMap<String, ArrayList<GrammarRule>>, Void, String>() {

            @Override
            protected String doInBackground(HashMap<String, ArrayList<GrammarRule>>... arg0) {
                ArrayList<GrammarRule> list = new ArrayList<>();
                getSentence(arg0[0], "ROOT", list);
                for (GrammarRule rule : list) {
                    System.out.print(rule.terminal);
                }

                return "";
            }

            @Override
            protected void onPostExecute(String res) {
                //mPostText.setText("");
                //refreshPostHistory();
            }
        }.execute(grammar);
    }

    private class GrammarRule {

        public String current;
        public ArrayList<String> others;
        public String terminal;

        private GrammarRule() {
            this.others = new ArrayList<>();
        }
    }
}



////using the grammar, consturct a list of rules to make a sentence and save those rules in ruleList
////Method should be used recursivly
//-(void) printSentenceWith: (NSMutableDictionary*) grammar startingWith: (NSString*) word andArray: (NSMutableArray*) ruleList {
//
//    //base case, if the word is not in the grammar, return
//    if (![grammar objectForKey:word]) {
//        NSLog(@"%@", word);
//
//    //if the word is in the grammar, then get the rules out of the grammar for that word
//    } else {
//        NSMutableArray* nextKeys = [grammar objectForKey:word];
//
//        //randomly select one of the rules
//        int randomKey = arc4random() % [nextKeys count];
//        GrammarRule* newRule = [nextKeys objectAtIndex:randomKey];
//        //If the rule has nonterminals, recurse for all of the nonterminals
//        if (newRule.nonTerminals != NULL) {
//            for (NSString* key in newRule.nonTerminals) {
//                [self printSentenceWith:grammar startingWith:key andArray:ruleList];
//            }
//        }
//        //If the rule has terminals, add the rule to the list.
//        if (newRule.terminal != NULL) {
//            [ruleList addObject:newRule];
//        }
//
//    }
//
//}
