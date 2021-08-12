import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
class Address { //�̫�L�XLinkedList�ϥΫ�[�J
    String addr; //�s��Address
    int len;  //�s�����
    String code; //�s��objectcode
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
    public static String normalize(String data) {//�ɹs
        while (data.length() < 6) {
            data = "0" + data;
        }
        return data;
    }
    public static String Add(String Address, int AddNumber) {
        int number = Integer.parseInt(Address,16);//16�i����10�i��
        number += AddNumber;
        String Hex = Integer.toHexString(number).toUpperCase();//10�i����16�i��
        //���פ���4���ɭԸ�0
        while (Hex.length() < 4) {
            Hex = "0" + Hex;
        }
        return Hex;
    }
    public static void main(String argv[]) throws Exception {
        Hash op = new Hash(); //optable
        Hash s = new Hash(); //�s��label,address
        op.init();
        FileReader fr = new FileReader("D:\\java\\SIC.asm"); //Ū��Ӫ��ɦW�OSIC.asm
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("pass1.txt");
        LinkedList<Object> errorList = new LinkedList<Object>(); //����~�T����LinkedList
        String line;
        String Address = "0000";
        int a = 0; //���
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~���Ĥ@��~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        while((line = br.readLine())!=null){
            a++; //���
            if(line.contains(".")){ //�p�G���I,���N���h��
                line = line.substring(0, line.indexOf(".")).trim();   
            } else {
                line = line.trim(); //�ťեh��
            }
            if(!line.equals("")){  //�Ŧ�h��
                String[] token = line.split("\\s+");//��token
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
                        errorList.add("��" +a + "��" +" :  Mnemonic ���~");
                    }
                }
                if(op.get(token[0]) != null){//�Ĥ@�Ӭ�Mnemonic
                /*
                       * 1.[mnemonic] [operand] ,X    ����3
                       * 2.[mnemonic] [operand] , X   ����4
                       * 3.[mnemonic] [operand],X     ����2
                       * 4.[mnemonic] [operand], X    ����3
                */
                    if(token.length > 2){
                        if(token[2].toUpperCase().contains("X")){ //����3
                            if(token[2].toUpperCase().equals(",X")){
                                fw.write(a+" "+ Address+" "+token[0]+" "+token[1].substring(0,token[1].indexOf(","))+"   index"+"\r\n");
                            }else if(token[2].toUpperCase().equals("X")){
                                fw.write(a+" "+ Address+" "+token[0]+" "+token[1].substring(0,token[1].indexOf(","))+"   index"+"\r\n");
                            }else{
                                errorList.add("��" +a + "��" +" :  ���i����X");
                            }
                        }
                        else if(op.get(token[0]) != null && op.get(token[1]) != null){ 
                            errorList.add("��" +a + "��" +" : label ���i��Mnemonic");// LDA LDA abc
                        }
                        else if(token[3].toUpperCase().contains("X")){ //����4
                            if(token[3].toUpperCase().equals("X")){
                                fw.write(a+" "+Address+" "+token[1]+" "+token[0]+"   index"+"\r\n");
                            }else{
                                errorList.add("��" +a + "��" +" :  ���i����X");
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
                            errorList.add("��" +a + "��" +" : label ���i��Mnemonic");//LDA LDA
                        }else if(token[0].equals("RSUB")){ //RSUB [operand]
                            errorList.add("��" +a + "��" +" :  RSUB�����~");
                        }
                        else if(token[1].equals("END")){
                            s.put("END",Address);
                            fw.write(a+" "+Address+" "+token[1]+" "+token[0]+"\r\n");
                            errorList.add("��" +a + "��" +" :  Label���i��Mnemonic");//LDA END
                        }
                        else{
                            fw.write(a+" "+Address+" "+token[0]+" "+token[1]+"\r\n");
                        }
                    }
                    Address = Add(Address, 3);
                    
                }
                else if(op.get(token[1]) != null){//�ĤG�Ӭ�Mnemonic
                    if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : ����Label");
                    }
                    else{
                        s.put(token[0],Address);
                        
                    /*
                           * 1.[label] [mnemonic] [operand] ,X    ����4
                           * 2.[label] [mnemonic] [operand] , X   ����5
                           * 3.[label] [mnemonic] [operand],X     ����3
                           * 4.[label] [mnemonic] [operand], X    ����4
                    */
                        if(token.length > 3){ 
                            if(token[3].toUpperCase().contains("X")){//����4
                                if(token[3].equals(",X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                                }else if(token[3].equals("X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                                }
                                else{
                                    errorList.add("��" +a + "��" +" :  ���i����X");
                                }
                            }
                            if(token[4].toUpperCase().contains("X")){//����5
                                if(token[4].equals("X")){
                                    fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"   index"+"\r\n");
                                }else{
                                    errorList.add("��" +a + "��" +" :  ���i����X");
                                }
                            }
                        }
                        if(token.length == 3){
                            if(token[2].toUpperCase().contains("X")){
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2].substring(0,token[2].indexOf(","))+"   index"+"\r\n");
                            }
                            else if(token[1].equals("RSUB")){ //A RSUB B
                                errorList.add("��" +a + "��" +" : RSUB�����~");
                            }
                            else{
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                            }
                        }
                        if(token.length ==2){ //Ex: RSUB //��o��
                            if(token[1].equals("RSUB")){
                                fw.write(a+" "+Address+" "+token[0]+" "+token[1]+"\r\n");
                            }else{
                                 errorList.add("��" +a + "��" +" : Mnemonic���~");
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
                        errorList.add("��" +a + "��" +" : END �����~");
                    }else if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : END �e��Label���Ʃw�q");
                    }
                    else{
                        System.out.println();
                    }
                    break;
                }
                else if(token[1].toUpperCase().equals("RESB")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : ����Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+ token[2]+"\r\n");
                        Address = Add(Address, Integer.parseInt(token[2]));
                    }
                }
                else if(token[1].toUpperCase().equals("RESW")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : ����Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1] +" "+ token[2]+"\r\n");
                        Address = Add(Address, Integer.parseInt(token[2]) * 3);
                    }
                }
                else if(token[1].toUpperCase().equals("WORD")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : ����Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                        Address = Add(Address, 3);
                    }
                }
                else if(token[1].toUpperCase().equals("BYTE")){
                    if(s.put(token[0],Address) == false){
                        errorList.add("��" +a + "��" +" : ����Label");
                    }
                    else{
                        s.put(token[0],Address);
                        fw.write(a+" "+Address+" "+token[0]+" "+token[1]+" "+token[2]+"\r\n");
                        
                        if(token[2].charAt(0) == 'C'){
                            int BYTEadd = (token[2].length() - 3);
                            Address = Add(Address ,BYTEadd);
                        }
                        if(token[2].charAt(0) == 'X'){ //�u��񰸼ƭ�
                            if((token[2].substring(2, token[2].length() - 1).length()) % 2 == 1 ){
                                errorList.add("��" + a + "��" +" : X' '���u�స�ƭ�");
                            }
                            else{
                                int BYTEadd = (token[2].length() - 3)/2;
                                Address = Add(Address ,BYTEadd);
                            }
                        }
                    }
                }
                else if(token.length ==2 ){ //�P�O���׵���2��Mneonic���~
                    if(op.get(token[0]) == null ){ //LDAA LENGTH
                        errorList.add("��" + a + "��" +" : Mnemonic error");
                    }
                }
                else if(token.length > 2){ //�P�O���פj��2��Mneonic���~
                    if(!token[1].equals("START") && !token[1].equals("END") && op.get(token[1]) == null ){//CLOOP JSUBB RDREC
                        errorList.add("��" + a + "��" + " : Mnemonic error");
                    }
                }
                else{//���L
                }
            }
            else{//���L
            }
        }
        fw.flush();
        fw.close();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~���ĤG��~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        FileReader frMid = new FileReader("D:\\java\\pass1.txt"); //Ū�i�Ӫ��ɦW�Opass1.txt
        BufferedReader brMid = new BufferedReader(frMid);
        String line2; 
        LinkedList<Object> b = new LinkedList<Object>();

        while((line2 = brMid.readLine())!=null){
            
            String[] token2 = line2.split("\\s+"); //�� token2
            if(token2[2].equals("END")){
                    b.add(new Address("",0,"E^00"+s.get(token2[3])));
                break;
            }
            else if(op.get(token2[2]) != null){//�p�G�Ĥ@�ӬOMnemonic
                if(token2.length > 4){
                    if(token2[4].equals("index")){ //Ex: 38 204E STCH BUFFER   index
                        if(s.get(token2[3]) == null){
                            errorList.add("��" + token2[0] + "��" +" : undefined Symbol");
                        }else{
                            b.add(new Address(token2[1], 6 ,(op.get(token2[2])+(Integer.parseInt(s.get(token2[3]), 16)+8000))));
                        }
                    }else{//���L
                    }
                }
                else if(token2.length == 4){ //Ex: 20 1024 LDL RETADR
                    if(s.get(token2[3]) == null){
                        errorList.add("��" + token2[0] + "��" +" : undefined Symbol");
                    }else{
                        b.add(new Address(token2[1], 6 ,(op.get(token2[2])+s.get(token2[3]))));
                    }
                }
                else if(token2.length == 3){//EX: 42 205A RSUB
                    b.add(new Address(token2[1], 6 ,"4C0000"));
                }
                else{//���L
                }
            }
            else if(op.get(token2[3]) != null){//�p�G�ĤG�ӬOMnemonic
                if(token2.length == 6){
                    if(token2[5].equals("index")){ //Ex: 15 1015 ENDFIL LDA EOF index
                        if(s.get(token2[4]) == null){
                            errorList.add("��" + token2[0] + "��" +" : undefined Symbol");
                        }else{
                            b.add(new Address(token2[3], 6 ,op.get(token2[3])+(Integer.parseInt(s.get(token2[4]), 16)+8000)));
                        }
                    }
                    else{//���L
                    }
                }
                else if(token2.length == 5){ //Ex: 15 1015 ENDFIL LDA EOF
                    if(s.get(token2[4]) == null){
                        errorList.add("��" + token2[0] + "��" +" : undefined Symbol");
                    }else{
                        b.add(new Address(token2[1], 6 ,op.get(token2[3])+s.get(token2[4])));
                    }
                }else if(token2.length == 4){ //Ex: 56 2076 TEST RSUB
                    b.add(new Address(token2[1], 6 , "4C0000"));
                }
                else{//���L
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
                if(token2[4].charAt(0) == 'C'){ //�d��ASC||
                    String Label = token2[4].substring(2, token2[4].length() - 1); //��X' '�����r
                    for (int i = 0; i < Label.length(); i++) { //�ন16�i�� ascii �A�[�J
                        b.add(new Address(token2[1], 2 ,String.valueOf(Integer.toHexString((int)Label.charAt(i))).toUpperCase()));//String.valueOf �N���ഫ���r�ꫬ�A
                    }
                }
                else if(token2[4].charAt(0) == 'X'){ 
                    b.add(new Address(token2[1],(token2[4].substring(2, token2[4].length() - 1)).length(),token2[4].substring(2, token2[4].length() - 1)));//��X' '�����r
                }
                else{//���L
                }
            }
            else{//���L
            }
            
        }
        for (Object e : errorList) { //�L�XerrorList
            System.out.println(e);
        }

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~�L�XObjectProgram~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        FileWriter fw2 = new FileWriter("ObjectProgram.txt");
        //���L�X H^~~~
        System.out.println("H^"+s.get("raven")+" "+"^00"+s.get("START")+"^"+"00"+Integer.toHexString(Integer.parseInt(s.get("END"),16)-Integer.parseInt(s.get("START"),16)).toUpperCase());
        fw2.write("H^"+s.get("raven")+" "+"^00"+s.get("START")+"^"+"00"+Integer.toHexString(Integer.parseInt(s.get("END"),16)-Integer.parseInt(s.get("START"),16)).toUpperCase()+"\r\n");
        Address tmp = (Address)b.getFirst();//���O��Address�A���o�Ĥ@�ӭȵ�tmp
        int tLength = 0;//���ת�l��0
        String tRecord = "";//tRecord��l�Ŧr��
        String tStart = tmp.addr;//���Ĥ@�ӭȪ�addr
        String eRecord = "";//eRecord��l�Ŧr��,�LE^�Ϊ�
        while (tmp != null) {
            if(tmp.code.equals("RESW")||tmp.code.equals("RESB")) {
                if (tLength > 0) { //�o�榳�F��
                    System.out.println("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord);
                    fw2.write("T00"+ tStart + "^" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord+"\r\n");
                    tRecord = "";//tRecord�ܪŦr��
                    tStart = tmp.addr;
                    tLength = 0;//�����k�s
                }
                tStart = Integer.toHexString(Integer.parseInt(tStart, 16) + tmp.len); // ���O���O16�i�� len �O �Q�i��
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
                    tLength = 0;//�����k�s
                }
                tLength += tmp.len;
                tRecord += tmp.code;
            }
            b.removeFirst();
            tmp = (Address)b.getFirst();
        }
        if (tLength > 0) { //�N�ѤU�����F��L�X(�S���J��RESB,RESW�A�B���פp��60)
            System.out.println("T00"+ tStart + "^0" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord);
            fw2.write("T00"+ tStart + "^0" + Integer.toHexString(tLength/2).toUpperCase() + "^" + tRecord+"\r\n");
        }
        System.out.println(eRecord);//�L�XE^
        fw2.write(eRecord+"\r\n");
        
        fw2.flush();
        fw2.close();
    }
}