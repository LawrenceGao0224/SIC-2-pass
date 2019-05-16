import java.util.Scanner;
class Node {
    String key,value;
    Node next;
}
public class Hash {
    int n;
    Node[] data;
    public Hash(){
        n = 10;
        data = new Node[10];//�إ߸�ư}�C
    }
    public boolean put(String key,String value){//��J���
        int slot;//�إߴ���
        if(key.hashCode() < 0)//�p�G�O�t��
            slot = -key.hashCode() % n;//�[�t�������A�o��l��
        else 
            slot = key.hashCode() %n;
        Node tmp;
        for (tmp = data[slot]; tmp != null; tmp = tmp.next)
            if (tmp.key.equals(key))
                break;
        if (tmp != null) {//duplicate key
            
            return false;
        }else{
            tmp = new Node();
            tmp.key = key;
            tmp.value = value;
            tmp.next = data[slot];//���Ʊ��W
            data[slot] = tmp;
        return true;
        }
    }
    public String get(String key){//����
        int slot;//�����Ʃ�b�l�Ƭ��h�֪�slot
        if (key.hashCode() < 0)//�p�G�O�t��
            slot = -key.hashCode() % n;//�[�t�������A�o��l��
        else
            slot = key.hashCode() % n;
        Node tmp;//�n�䪺��
        for (tmp = data[slot]; tmp != null; tmp = tmp.next)
            if (tmp.key.equals(key))
                break;
        if (tmp != null)
            return tmp.value;
        return null;
    }
    public void remove(String key) {//�������
        int slot;
        if (key.hashCode() < 0)//�p�G�O�t��
            slot = -key.hashCode() % n;//�[�t�������A�o��l��
        else
            slot = key.hashCode() % n;
        Node tmp, prev;
        tmp = prev = null;
        //�ΰj��h��nremove����
        for (tmp = data[slot]; tmp != null; tmp = tmp.next) { 
            if (tmp.key.equals(key)) {
                break;
            }
            prev = tmp;
        }
        if (tmp == null){
            return;
        }
        if (prev == null) {
            data[slot] = tmp.next;
        } else {
            prev.next = tmp.next;//�N�e�@��ƻP�᭱��Ƭ۳s��
        }
    }
    public void init(){
        // Hash h = new Hash();
        put("MULF","60");
        put("MULR","98");
        put("NORM","C8");
        put("OR","44");
        put("RD","D8");
        put("RMO","AC");
        put("RSUB","4C");
        put("SHIFTL","A4");
        put("SHIFTR","A8");
        put("SIO","F0");
        put("SSK","EC");
        put("STA","0C");
        put("STB","78");
        put("STCH","54");
        put("STF","80");
        put("STI","D4");
        put("STL","14");
        put("STS","7C");
        put("STSW","E8");
        put("STT","84");
        put("STX","10");
        put("SUB","1C");
        put("SUBF","5C");
        put("SUBR","94");
        put("SVC","B0");
        put("TD","E0");
        put("TIO","F8");
        put("TIX","2C");
        put("TIXR","B8");
        put("WD","DC");
        put("ADD","18");
        put("ADDF","58");
        put("ADDR","90");
        put("AND","40");
        put("CLEAR","B4");
        put("COMP","28");
        put("COMPF","88");
        put("COMPR","A0");
        put("DIV","24");
        put("DIVF","64");
        put("DIVR","9C");
        put("FIX","C4");
        put("FLOAT","C0");
        put("HIO","F4");
        put("J","3C");
        put("JEQ","30");
        put("JGT","34");
        put("JLT","38");
        put("JSUB","48");
        put("LDA","00");
        put("LDB","68");
        put("LDCH","50");
        put("LDF","70");
        put("LDL","08");
        put("LDS","6C");
        put("LDT","74");
        put("LDX","04");
        put("LPS","D0");
        put("MUL","20");
    }
}            
            