package datacite.oai.provider.util;

import java.io.PrintWriter;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;


/**
 * Creates a PrintWriter that logs all the print/println information to the category used in the constructor
 */
public class Log4jPrintWriter extends PrintWriter {
  Priority level;
  Category cat;
  StringBuilder text = new StringBuilder("");

  public Log4jPrintWriter(org.apache.log4j.Category cat, org.apache.log4j.Priority level) {
    super(System.err);  // PrintWriter doesn't have default constructor.
    this.level =level;
    this.cat   = cat;
  }

  // overrides all the print and println methods for 'print' it to the constructor's Category
  public void close(){
   flush();
  }
  public void flush(){
   if (!text.toString().equals("")){
    cat.log(level,text.toString());
    text.setLength(0);
   }
  }
  public void print(boolean b){
   text.append(b);
  }

  public void print(char c){
   text.append(c);
  }
  public void print(char[] s){
   text.append(s);
  }
  public void print(double d){
   text.append(d);
  }
  public void print(float f){
   text.append(f);
  }
  public void print(int i){
   text.append(i);
  }
  public void print(long l){
   text.append(l);
  }
  public void print(Object obj){
   text.append(obj);
  }
  public void print(String s){
   text.append(s);
  }
  public void println(){
   if (!text.toString().equals("")){
    cat.log(level,text.toString());
    text.setLength(0);
   }
  }
  public void println(boolean x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(char x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(char[] x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(double x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(float x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(int x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(long x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(Object x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
  public void println(String x){
   text.append(x);
   cat.log(level,text.toString());
   text.setLength(0);
  }
}

