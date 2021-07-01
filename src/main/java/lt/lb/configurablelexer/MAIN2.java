/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.configurablelexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lt.lb.commons.DLog;

/**
 *
 * @author laim0nas100
 */
public class MAIN2 {
    public static void main(String[] args) {
        Pattern compile = Pattern.compile("\\d+\\.\\d+");
        String input = "labas 100.10 ok";
        Matcher matcher = compile.matcher(input);
        
        DLog.print(matcher);
        
        
        boolean find = matcher.find(5);
        
        DLog.print(matcher);
        
        DLog.print(matcher.start());
        DLog.print(matcher.end());
        DLog.close();
    }
}
