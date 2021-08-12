import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
class Address { //最後印出LinkedList使用後加入
    String addr; //存放Address
    int len;  //存放長度
    String code; //存放objectcode
    Address(String a, int l, String cd) { 
        addr = a; 
        len = l; 
        code = cd; 
    } 
    public String toString() { 
        return addr + " " + len   + " " + code; 
    } 
}
public class SIC{
    public static String normalize(String data) {//補零
        while (data.length() < 6) {
            data = "0" + data;
        }
        return data;
    }
    public static String Add(String Address, int AddNumber) {
        int number = Integer.parseInt(Address,16);//16進位轉10進位
        number += AddNumber;
        String Hex = Integer.toHexString(number).toUpperCase();//10進位轉16進位
        //長度不足4的時候補0
        while (Hex.length() < 4) {
            Hex = "0" + Hex;
        }
        return Hex;
    }
    public static void main(String argv[]) throws Exception {
        Hash op = new Hash(); //optable
        Hash s = new Hash(); //存放label,address
        op.init();
        FileReader fr = new FileReader("D:\\java\\SIC.asm"); //讀近來的檔名是SIC.asm
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("pass1.txt");
        LinkedList<Object> errorList = new LinkedList<Object>(); //放錯誤訊息的LinkedList
        String line;
        String Address = "0000";
        int a = 0; //行數
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~掃第一次~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        while((line = br.readLine())!=null){
            a++; //行數
            if(line.contains(".")){ //如果有點,其後就都去除
                line = line.substring(0, line.indexOf(".")).trim();   
            } else {
                line = line.trim(); //空白去除
            }
            if(!line.equals("")){  //空行去除
                String[] token = line.split("\\s+");//切token
                if(token.length > 2){
                    if(token[1].toUpperCase().equals("START")){
                        if(token[1] != null){
                            s.put("START",token[2]);
                        }else{
                            s.put("START","0000");
                        }
                        s.put("raven",token[0]);
                        Address = token[2];
                    }
                }
                else if(token.length == 1 && op.get(token[0]) != null){ //EX: RSUB 
                    if(token[0].equals("RSUB")){
                        fw.write(a+" "+Address+" "+token[0]+"\r\n");
                    }else{
                        errorList.add("第" +a + "行" +" :  Mnemonic 錯誤");
                    }
                }
                if(op.get(token[0]) != null){//第一個為Mnemonic
                /*
                       * 1.[mnemonic] [operand] ,X    長度3
                       * 2.[mnemonic] [operand] , X   長度4
                       * 3.[mnemonic] [operand],X     長度2
                       * 4.[mnemonic] [operand], X    長度3
                */
                    if(token.length > 2){
                        if(token[2].toUpperCase().contains("X")){ //長度3
                            if(token[2].toUpperCase().equals(",X")){
                                fw.write(a+" "+ Address+" "+token[0]+" "+token[1].substring(0,token[1].indexOf(","))+"   index"+"\r\n");
                            }else if(token[2].toUpperCase().equals("X")){
                                fw.write(a+" "+ Address+" "+token[0]+" "+token[1].substring(0,token[1].indexOf(","))+"   index"+"\r\n");
                            }else{
                                errorList.add("第" +a + "行" +" :  不可重複X");
                            }
                        }
                        else if(op.get(token[0]) != null && op.get(token[1]) != null){ 
                            errorList.add("第" +a + "行" +" : label 不可為Mnemonic");// LDA LDA abc
                        }
                        else if(token[3].toUpperCase().contains("X")){ //長度4
                            if(token[3].toUpperCase().equals("X")){
                                fw.write(a+" "+Address+" "+token[1]+" "+token[0]+"   index"+"\r\n");
                            }else{
                                errorList.add("第" +a + "行" +" :  不可重複X");
                            }
                        }
                        else{
                            fw.write(a+" "+Address+" "+token[0]+" "+token[1]+"\r\n");
                        }
                    }
                    if(token.length == 2){
                        if(token[1].contains(",")){
                            fw.write(a+" "+Address+" "+token[0]+" "+token[1].substring(0,token[1].indexOf(","))+"   index"+"\r\n");
                        }
                        else if(op.get(token[0]) != null && op.get(token[1]) != null){ 
                            errorList.add("第" +a + "行" +" : label 不可為Mnemonic");//LDA LDA
                        }else if(token[0].equals("RSUB")){ //RSUB [operand]
                            errorList.add("第" +a + "行" +" :  RSUB後方錯誤");
                        }
                        else if(token[1].equals("END")){
                            s.put("END",Address);
                            fw.write(a+" "+Address+" "+token[1]+" "+token[0]+"\r\n");
                            errorList.add("第" +a + "行" +" :  Label不可為Mnemonic");//LDA END
                        }
                        else{
                            fw.write(a+" "+Address+" "+token[0]+" "+token[1]+"\r\n");
                        }
                    }
                    Address = Add(Address, 3);
                    
                }
                else if(op.get(token[1]) != null){//第二個為Mnemonic
                    if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : 重複Label");
                    }
                    else{
                        s.put(token[0],Address);
                        
                    /*
                           * 1.[label] [mnemonic] [operand] ,X    長度4
                           * 2.[label] [mnemonic] [operand] , X   長度5
                           * 3.[label] [mnemonic] [operand],X     長度3
                           * 4.[label] [mnemonic] [operand], X    長度4
                    */
                        if(token.length > 3){ 
                            if(token[3].toUpperCase().contains("X")){//長度4
                                if(token[3].equals(",X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                                }else if(token[3].equals("X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                                }
                                else{
                                    errorList.add("第" +a + "行" +" :  不可重複X");
                                }
                            }
                            if(token[4].toUpperCase().contains("X")){//長度5
                                if(token[4].equals("X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"   index"+"\r\n");
                                }else{
                                    errorList.add("第" +a + "行" +" :  不可重複X");
                                }
                            }
                        }
                        if(token.length == 3){
                            if(token[2].toUpperCase().contains("X")){
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                            }
                            else if(token[1].equals("RSUB")){ //A RSUB B
                                errorList.add("第" +a + "行" +" : RSUB後方錯誤");
                            }
                            else{
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                            }
                        }
                        if(token.length ==2){ //Ex: RSUB //改這行
                            if(token[1].equals("RSUB")){
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+"\r\n");
                            }else{
                                 errorList.add("第" +a + "行" +" : Mnemonic錯誤");
                            }
                        }
                        
                        Address = Add(Address, 3);
                    }
                }
                else if(token[0].toUpperCase().equals("END") || token[1].equals("END") ){
                    s.put("END",Address);
                    fw.write(a+" "+Address+" "+"END"+" "+token[1]+"\r\n");
                    Address = Add(Address, 1);
                    if(token[0].equals("END")&& token[1].equals("END") || s.get(token[1]) == null){// END END or END LOOP
                        errorList.add("第" +a + "行" +" : END 後方錯誤");
                    }else if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : END 前方Label重複定義");
                    }
                    else{
                        System.out.println();
                    }
                    break;
                }
                else if(token[1].toUpperCase().equals("RESB")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : 重複Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+ token[2]+"\r\n");
                        Address = Add(Address, Integer.parseInt(token[2]));
                    }
                }
                else if(token[1].toUpperCase().equals("RESW")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : 重複Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1] +" "+ token[2]+"\r\n");
                        Address = Add(Address, Integer.parseInt(token[2]) * 3);
                    }
                }
                else if(token[1].toUpperCase().equals("WORD")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : 重複Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                        Address = Add(Address, 3);
                    }
                }
                else if(token[1].toUpperCase().equals("BYTE")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("第" +a + "行" +" : 重複Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                        
                        if(token[2].charAt(0) == 'C'){
                            int BYTEadd = (token[2].length() - 3);
                            Address = Add(Address ,BYTEadd);
                        }
                        if(token[2].charAt(0) == 'X'){ //只能放偶數個
                            if((token[2].substring(2, token[2].length() - 1).length()) % 2 == 1 ){
                                errorList.add("第" + a + "行" +" : X' '中只能偶數個");
                            }
                            else{
                                int BYTEadd = (token[2].length() - 3)/2;
                                Address = Add(Address ,BYTEadd);
                            }
                        }
                    }
                }
                else if(token.length ==2 ){ //判別長度等於2的Mneonic錯誤
                    if(op.get(token[0]) == null ){ //LDAA LENGTH
                        errorList.add("第" + a + "行" +" : Mnemonic error");
                    }
                }
                else if(token.length > 2){ //判別長度大於2的Mneonic錯誤
                    if(!token[1].equals("START") && !token[1].equals("END") && op.get(token[1]) == null ){//CLOOP JSUBB RDREC
                        errorList.add("第" + a + "行" + " : Mnemonic error");
                    }
                }
                else{//不印
                }
            }
            else{//不印
            }
        }
        fw.flush();
        fw.close();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~掃第二次~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        FileReader frMid = new FileReader("D:\\java\\pass1.txt"); //讀進來的檔名是pass1.txt
        BufferedReader brMid = new BufferedReader(frMid);
        String line2; 
        LinkedList<Object> b = new LinkedList<Object>();

        while((line2 = brMid.readLine())!=null){
            
            String[] token2 = line2.split("\\s+"); //切 token2
            if(token2[2].equals("END")){
                    b.add(new Address("",0,"E^00"+s.get(token2[3])));
                break;
            }
            else if(op.get(token2[2]) != null){//如果第一個是Mnemonic
                if(token2.length > 4){
                    if(token2[4].equals("index")){ //Ex: 38 204E STCH BUFFER   index
                        if(s.get(token2[3]) == null){
                            errorList.add("第" + token2[0] + "行" +" : undefined Symbol");
                        }else{
                            b.add(new Address(token2[1], 6 ,(op.get(token2[2])+(Integer.parseInt(s.get(token2[3]), 16)+8000))));
                        }
                    }else{//不印
                    }
                }
                else if(token2.length == 4){ //Ex: 20 1024 LDL RETADR
                    if(s.get(token2[3]) == null){
                        errorList.add("第" + token2[0] + "行" +" : undefined Symbol");
                    }else{
                        b.add(new Address(token2[1], 6 ,(op.get(token2[2])+s.get(token2[3]))));
                    }
                }
                else if(token2.length == 3){//EX: 42 205A RSUB
                    b.add(new Address(token2[1], 6 ,"4C0000"));
                }
                else{//不印
                }
            }
            else if(op.get(token2[3]) != null){//如果第二個是Mnemonic
                if(token2.length == 6){
                    if(token2[5].equals("index")){ //Ex: 15 1015 ENDFIL LDA EOF index
                        if(s.get(token2[4]) == null){
                            errorList.add("第" + token2[0] + "行" +" : undefined Symbol");
                        }else{
                            b.add(new Address(token2[3], 6 ,op.get(token2[3])+(Integer.parseInt(s.get(token2[4]), 16)+8000)));
                        }
                    }
                    else{//不印
                    }
                }
                else if(token2.length == 5){ //Ex: 15 1015 ENDFIL LDA EOF
                    if(s.get(token2[4]) == null){
                        errorList.add("第" + token2[0] + "行" +" : undefined Symbol");
                    }else{
                        b.add(new Address(token2[1], 6 ,op.get(token2[3])+s.get(token2[4])));
                    }
                }else if(token2.length == 4){ //Ex: 56 2076 TEST RSUB
                    b.add(new Address(token2[1], 6 , "4C0000"));
                }
                else{//不印
                }
            }
            else if(token2[3].equals("RESW")){//RESW
                b.add(new Address(token2[1], Integer.parseInt(token2[4])*3 ,"RESW"));
            }
            else if(token2[3].equals("RESB")){//RESB
                b.add(new Address(token2[1], Integer.parseInt(token2[4]) ,"RESB"));
            }
            else if(token2[3].equals("WORD")){
                b.add(new Address(token2[1], 6 ,normalize(Integer.toHexString(Integer.parseInt(token2[4])))));
            }
            else if(token2[3].equals("BYTE")){
                if(token2[4].charAt(0) == 'C'){ //查找ASC||
                    String Label = token2[4].substring(2, token2[4].length() - 1); //抓出' '中的字
                    for (int i = 0; i < Label.length(); i++) { //轉成16進位 ascii 再加入
                        b.add(new Address(token2[1], 2 ,String.valueOf(Integer.toHexString((int)Label.charAt(i))).toUpperCase()));//String.valueOf 將其轉換成字串型態
                    }
                }
                else if(token2[4].charAt(0) == 'X'){ 
                    b.add(new Address(token2[1],(token2[4].substring(2, token2[4].length() - 1)).length(),token2[4].substring(2, token2[4].length() - 1)));//抓出' '中的字
                }
                else{//不印
                }
            }
            else{//不印
            }
            
        }
        for (Object e : errorList) { //印出errorList
            System.out.println(e);
        }

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~印出ObjectProgram~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        FileWriter fw2 = new FileWriter("ObjectProgram.txt");
        //先印出 H^~~~
        System.out.println("H^"+s.get("raven")+" "+"^00"+s.get("START")+"^"+"00"+Integer.toHexString(Integer.parseInt(s.get("END"),16)-Integer.parseInt(s.get("START"),16)).toUpperCase());
        fw2.write("H^"+s.get("raven")+" "+"^00"+s.get("START")+"^"+"00"+Integer.toHexString(Integer.parseInt(s.get("END"),16)-Integer.parseInt(s.get("START"),16)).toUpperCase()+"\r\n");
        Address tmp = (Address)b.getFirst();//類別為Address，取得第一個值給tmp
        int tLength = 0;//長度初始為0
        String tRecord = "";//tRecord初始空字串
        String tStart = tmp.addr;//給第一個值的addr
        String eRecord = "";//eRecord初始空字串,印E^用的
        while (tmp != null) {
            if(tmp.code.equals("RESW")||tmp.code.equals("RESB")) {
                if (tLength > 0) { //這行有東西
                    System.out.println("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord);
                    fw2.write("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord+"\r\n");
                    tRecord = "";//tRecord變空字串
                    tStart = tmp.addr;
                    tLength = 0;//長度歸零
                }
                tStart = Integer.toHexString(Integer.parseInt(tStart, 16) + tmp.len); // 但是它是16進位 len 是 十進位
            }
            else if(tmp.code.contains("E^00")){
                eRecord = tmp.code;
                break;
            }
            else {
                if (tLength + tmp.len > 60) {
                    System.out.println("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord);
                    fw2.write("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord+"\r\n");
                    tRecord = "";
                    tStart = tmp.addr;
                    tLength = 0;//長度歸零
                }
                tLength += tmp.len;
                tRecord += tmp.code;
            }
            b.removeFirst();
            tmp = (Address)b.getFirst();
        }
        if (tLength > 0) { //將剩下有的東西印出(沒有遇到RESB,RESW，且長度小於60)
            System.out.println("T00"+ tStart + "^0" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord);
            fw2.write("T00"+ tStart + "^0" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord+"\r\n");
        }
        System.out.println(eRecord);//印出E^
        fw2.write(eRecord+"\r\n");
        
        fw2.flush();
        fw2.close();
    }
}