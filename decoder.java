import java.util.*;
import java.io.*;
public class decoder {

	public static void main(String[] args) {
		long starttime=System.currentTimeMillis();
		ArrayList<Integer> al=new ArrayList<Integer>();
		HashMap<String,Integer> hm=new HashMap<String,Integer>();
		InputStream is=null,binaryis=null;
		String line,writeline="";
		int a=0;
		File file=null,binaryfile=null;
		ArrayList<Integer> originalmsg=new ArrayList<Integer>();
		HashMap<Integer,String> map=new HashMap<Integer,String>();
		try{
			// Reading from Code Table
			String path=args[1];
			is=new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while((line=reader.readLine())!=null){
				if(!line.equals("")){
					String[] str=line.split(" ");
					hm.put(str[1],Integer.parseInt(str[0]));
				}
			}
			
			// Reading from encoded bin file.
			String path1=args[0];
			binaryfile=new File(path1);
			binaryis = new BufferedInputStream(new FileInputStream(binaryfile));
			
			int len=(int) binaryfile.length();
			byte[] buffer=new byte[len];
	        int totalBytesRead = 0;
	        while(totalBytesRead < buffer.length){
	            int bytesRemaining = buffer.length - totalBytesRead;
	            int bytesRead = binaryis.read(buffer, totalBytesRead, bytesRemaining);
	            if (bytesRead > 0){
	                totalBytesRead = totalBytesRead + bytesRead;
	            }
	        }
	        Node root=new Node(-1,'c',null,null);
	        BuildTree tree=new BuildTree();
	        //Building a Decoder tree from Code table.
	        for(Map.Entry<String, Integer> me:hm.entrySet()){
	        	String str=me.getKey();
	        	int val=me.getValue();
	        	tree.buildTree(root,str,0,val);
	        		
	        	}
	        
	        // Decoding the input messages using Decoder Tree.
	        String dstr="";
	        int size=0;
	        String res="";
	        File op=new File("decoded.txt");
			OutputStream output=new FileOutputStream(op);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
			System.out.println("Started decoding");
			for(int i=0;i<buffer.length;i++){
	        	int x=buffer[i] & 0xFF;
	        	String s1 = String.format("%08d", Integer.parseInt(Integer.toBinaryString(x)));
	        	res=tree.decodeData(root, res+s1, writer);	
	        	}
	        long endtime=System.currentTimeMillis();
	        //System.out.println("Time Difference "+(endtime-starttime));
	    }catch(Exception e){
 
		e.printStackTrace();
	}finally {
         if(is!=null)
			try {	
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
      }	
		

	}
}
class BuildTree {
	public void buildTree(Node node,String str,int index,int msg){
    	if(index==str.length()-1){
    		if(str.charAt(index)=='0'){
        		node.left=new Node(msg,'0',null,null);
    			node.left.setLeaf();
    		}
        	else{
        		node.right=new Node(msg,'1',null,null);
        		node.right.setLeaf();
        	}
    		return;
    	}
    	if(str.charAt(index)=='0' && node.left==null){
    		node.left=new Node(-1,'0',null,null);
    		buildTree(node.left,str,index+1,msg);
    	}
    	else if(str.charAt(index)=='1' && node.right==null){
    		node.right=new Node(-1,'1',null,null);
    		buildTree(node.right,str,index+1,msg);
    	}
    	else if(str.charAt(index)=='0' && node.left!=null)
    		buildTree(node.left,str,index+1,msg);
    	else
    		buildTree(node.right,str,index+1,msg);
    	
    }
	public HashMap<Integer,String> decodedMessage(Node root,HashMap<Integer,String> map,String str){
		if(root==null) return map;
		if(root.left==null && root.right==null){
			map.put(root.value,str);
			System.out.println(root.value+": "+str);
			return map;
		} 
		if(root.left!=null) decodedMessage(root.left,map,str+root.left.code);
		//System.out.println(root.index);
		if(root.right!=null) decodedMessage(root.right,map,str+root.right.code);
		return map;
	}
	public String decodeData(Node htree, String message, BufferedWriter bw)throws Exception{
        Node utree = htree;
        Node n = null;
        String residual = "";
        while(message.length()>0){
            for(int i=0; i<message.length();i++){
                residual = residual + message.charAt(i);
                if(message.charAt(i)=='0'){
                    n = htree.left;
                    htree = n;
                    if(n.getLeaf()){
                        bw.write(n.value + "\n");
                        bw.flush();
                        message = message.substring(i+1);
                        htree = utree;
                        residual = "";
                        break;
                    }
                }else if(message.charAt(i)=='1'){
                    n = htree.right;
                    htree = n;
                    if(n.getLeaf()){
                        bw.write(n.value + "\n");
                        bw.flush();
                        message = message.substring(i+1);
                        htree=utree;
                        residual = "";
                        break;
                    }
                }
            }
            if(residual.trim().length()>0){
               break;
            }
        }
        return residual;
    }
}
class Node {
char code;
int value;
boolean Leaf=false;
Node left=null,right=null;
public Node(int value,char code,Node left,Node right){
	this.value=value;
	this.code=code;
	this.left=left;
	this.right=right;
}
public void setLeaf(){
	this.Leaf=true;
}
public boolean getLeaf(){
	return this.Leaf;
}
}



