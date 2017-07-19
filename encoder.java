import java.io.*;
import java.util.*;
public class encoder {

	public static void main(String[] args) {
		long starttime=System.currentTimeMillis();
		ArrayList<Integer> al=new ArrayList<Integer>();
		HashMap<Integer,Integer> hm=new HashMap<Integer,Integer>();
		long start_time,end_time;
		InputStream is=null;
		String line,writeline="";
		int a=0;
		OutputStream os=null,binaryos=null;
		File file=null,binaryfile=null;
		HashMap<Integer,String> map=new HashMap<Integer,String>();
		ArrayList<MinHeapNode> result=null;
		try{
			String path=args[0];
			is=new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while((line=reader.readLine())!=null){
				if(!line.equals(""))
				al.add(Integer.parseInt(line));
			}
			
			// Constructing frequency table from input table.
			for(int i=0;i<al.size();i++) {
				int val=al.get(i); 
				hm.put(val, hm.get(val)!=null?hm.get(val)+1:1);
			}
			int[] data=new int[hm.size()];
			int[] freq=new int[hm.size()];
			
			for(Map.Entry<Integer, Integer> me:hm.entrySet())
			{
				data[a]=me.getKey();
				freq[a]=me.getValue();
				a++;
			}
			
			// Writing to a Code_Table File Declaration
			file=new File("code_table.txt");
			os=new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
			if (!file.exists()) {
				file.createNewFile();
			}
			
			//Writing to a Binary File Declaration
			binaryfile=new File("encoded.bin");
			binaryos = new BufferedOutputStream(new FileOutputStream(binaryfile));
			if (!binaryfile.exists()) {
				binaryfile.createNewFile();
			}
			
			// Huffman Tree using 4-way cache optimized Heap.
			Huffman4wayHeap huff4way=new Huffman4wayHeap();
			
			for(int i=0;i<1;i++){
				result=huff4way.buildHuffmanTree(data,freq);
			}
			
			//Huffman Tree using Biary Heap
			//HuffmanBinaryHeap huffbin=new HuffmanBinaryHeap();
			//huffbin.buildHuffmanTree(data,freq);
			
			//Huffman Tree using Biary Heap
			//HuffmanPairHeap huffpairHeap=new HuffmanPairHeap();
			//huffpairHeap.buildHuffmanTree(data,freq);
			
			// Writing  Code Table to a file.
			for(int i=0;i<result.size();i++){
				MinHeapNode root=result.get(i);
				map.put(root.index,root.code);
				writeline=""+root.index+" "+root.code;
				writer.write(writeline);
				writer.newLine();
				writer.flush();
			}
			String emsg="";
			// Writing Encoded Message to a binary File.
			System.out.println("encoding");
			for(int i=0;i<al.size();i++){
				emsg=emsg+map.get(al.get(i));
				if(emsg.length()%8 ==0){
				for (int j = 0; j < emsg.length(); j += 8) {
		        String byteString = emsg.substring(j, j + 8);
		        int parsedByte = 0xFF & Integer.parseInt(byteString, 2);
		        binaryos.write((byte)parsedByte);
		        binaryos.flush();
		        }
				emsg="";
				
				
		    }
				
		}
			long endtime=System.currentTimeMillis();
			//System.out.println("Time Difference:"+(endtime-starttime));  
			
			
	
	}catch(Exception e){
			e.printStackTrace();
		}finally {
	         if(is!=null || os != null)
				try {	
					is.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	      }	
	}
}
class MinHeapNode {
		public int index;
		public int frequency;
		public MinHeapNode left=null;
		public MinHeapNode right=null;
		public String code;
		public void setCode(String code){
			this.code=code;
		}
		public MinHeapNode(int index,int frequency){
			this.index=index;
			this.frequency=frequency;
		}
		public void setLeft(MinHeapNode left){
			this.left=left;
		}
		public void setRight(MinHeapNode right){
			this.right=right;
		}
		public int getFrequency(){
			return frequency;
		}
}
class Huffman4wayHeap {
	ArrayList<MinHeapNode> al=null;
public ArrayList<MinHeapNode> buildHuffmanTree(int[] data,int[] freq){
	al=new ArrayList<MinHeapNode>();
	MinHeap minheap=createandBuildMinHeap(data,freq);
	while(minheap.getSize()>3)
	{
		MinHeapNode left=minheap.extractMin();
		MinHeapNode right=minheap.extractMin();
		MinHeapNode root=new MinHeapNode(Integer.MAX_VALUE,left.getFrequency()
				+right.getFrequency());
		root.setLeft(left);
		root.setRight(right);
		minheap.insertNode(root);
	}
	MinHeapNode root=minheap.extractMin();
	assignCodes(root,"");
	return al;
}
public void assignCodes(MinHeapNode root,String code){ 
	if(root==null) return;
	if(root.left==null && root.right==null){
		root.code=code;
		al.add(root);
		return;
	} 
	if(root.left!=null) assignCodes(root.left,code+'0');
	if(root.right!=null) assignCodes(root.right,code+'1');
}
public MinHeap createandBuildMinHeap(int[] data,int[] freq)
{
	MinHeapNode[] tree=new MinHeapNode[freq.length];
	for(int i=0;i<freq.length;i++)
	{
		tree[i]=new MinHeapNode(data[i],freq[i]);
		
	}
	MinHeap heap=new MinHeap(tree);
	return heap;
}
class MinHeap {
	private MinHeapNode[] tempHeap;
	private int size=2;
	private int capacity;
	private int offset=3;
	public MinHeap(MinHeapNode[] tree)
	{
		this.tempHeap=new MinHeapNode[tree.length+3];
		capacity=tree.length+3;
		for(int i=0;i<3;i++){
			tempHeap[i]=null;
		}
		for(int i=0;i<tree.length;i++)
			insertNode(tree[i]);
	}
	public MinHeapNode extractMin()
	{
		MinHeapNode min=tempHeap[3];
		tempHeap[3]=tempHeap[size];
		tempHeap[size]=null;
		size--;
		minHeapify(3);
		return min;
	}
	public void insertNode(MinHeapNode node)
	{
		if(size<capacity)
		{
			size++;
			tempHeap[size]=node;
			int current=size;
			while (isValid(current)
					&& isValid(getParent(current))
					&& tempHeap[getParent(current)].getFrequency() > tempHeap[current]
							.getFrequency()) {
				swap(getParent(current), current);
				current = getParent(current);
			}
		}
	}
	public void minHeapify(int index)
	{
		int smallest=findSmallChild(getChild1(index),getChild4(index));
		if(isValid(smallest) && tempHeap[smallest].getFrequency()<tempHeap[index].getFrequency())
		{
			swap(smallest,index);
			minHeapify(smallest);
		}
	}
	private int findSmallChild(int low,int high){
		int minChild=low;
		for(int i=low+1;i<=high && i<=size;i++){
			if(tempHeap[minChild].getFrequency()>tempHeap[i].getFrequency())
				minChild=i;
		}
		return minChild;
	}
	private void swap(int smallest,int index)
	{
		MinHeapNode temp=tempHeap[smallest];
		tempHeap[smallest]=tempHeap[index];
		tempHeap[index]=temp;
	}
	private boolean isValid(int index)
	{
		if(index>=3 && index<=size)
			return true;
		return false;
	}
	public int getSize(){
		return size;
	}
	public int getChild1(int i)
	{
		return (i-offset)*4+offset+1;
	}
	public int getChild2(int i)
	{
		return (i-offset)*4+offset+2;
	}
	public int getChild3(int i)
	{
		return (i-offset)*4+offset+3;
	}
	public int getChild4(int i)
	{
		return (i-offset)*4+offset+4;
	}
	
	public int getParent(int i)
	{
		return (int)Math.floor((i-1-offset)/4)+offset;
	}
}
}
class HuffmanPairHeap {
public void buildHuffmanTree(int[] data,int[] freq){
	PairHeap minheap=createandBuildMinHeap(data,freq);
	while(minheap.getSize()>0)
	{
		PairHeapNode left=minheap.extractMin();
		PairHeapNode right=minheap.extractMin();
		PairHeapNode root=new PairHeapNode(Integer.MAX_VALUE,left.getFrequency()
				+right.getFrequency());
		root.setLeft(left);
		root.setRight(right);
		minheap.insertNode(root);
	}
	PairHeapNode root=minheap.extractMin();
	//assignCodes(root,"");
	return;
}
public void assignCodes(PairHeapNode root,String code)
{ 
	if(root==null) return;
	if(root.left==null && root.right==null){
		root.code=code;
		System.out.println(root.index+": "+root.code);
		return;
	} 
	if(root.left!=null) assignCodes(root.left,code+'0');
	if(root.right!=null) assignCodes(root.right,code+'1');
}
public PairHeap createandBuildMinHeap(int[] data,int[] freq)
{
	PairHeapNode[] tree=new PairHeapNode[freq.length];
	for(int i=0;i<freq.length;i++)
	{
		tree[i]=new PairHeapNode(data[i],freq[i]);
		
	}
	PairHeap heap=new PairHeap(tree);
	return heap;
}

class PairHeapNode{
	private int index;
	private int frequency;
	private PairHeapNode left=null;
	private PairHeapNode right=null;
	private PairHeapNode leftChild;
	private PairHeapNode nextSibling;
	private PairHeapNode prev;
	private String code;	
	public void setCode(String code){
		this.code=code;
	}
	public PairHeapNode(int index,int frequency){
		this.index=index;
		this.frequency=frequency;
		left=null;
		prev=null;
		nextSibling=null;
	}
	public void setLeft(PairHeapNode left){
		this.left=left;
	}
	public void setRight(PairHeapNode right){
		this.right=right;
	}
	public int getFrequency(){
		return frequency;
	}
}
class PairHeap{
	private PairHeapNode[] treeArray = new PairHeapNode[5];;
	private int size=-1;
	private PairHeapNode root=null;
	public PairHeap(PairHeapNode[] tree){
		for(int i=0;i<tree.length;i++)
			insertNode(tree[i]);
	}
	public PairHeapNode extractMin(){
		if (size<0)
			return null;
		PairHeapNode min = root;
		size--;
		if (root.leftChild == null)
			root = null;
		else
			root = combineSiblings(root.leftChild);
		return min;
	}
	private PairHeapNode combineSiblings(PairHeapNode firstSibling) {
		if (firstSibling.nextSibling == null)
			return firstSibling;
		int numSiblings = 0;
		for (; firstSibling != null; numSiblings++) {
			treeArray = doubleIfFull(treeArray, numSiblings);
			treeArray[numSiblings] = firstSibling;
			firstSibling.prev.nextSibling = null;
			firstSibling = firstSibling.nextSibling;
		}
		treeArray = doubleIfFull(treeArray, numSiblings);
		treeArray[numSiblings] = null;
		int i = 0;
		while(i+1<numSiblings){
			treeArray[i] = compareAndLink(treeArray[i], treeArray[i + 1]);
			i=i+2;
		}
		int j = i - 2;
		if (j == numSiblings - 3)
			treeArray[j] = compareAndLink(treeArray[j], treeArray[j + 2]);
			while(j>=2){
			treeArray[j - 2] = compareAndLink(treeArray[j - 2], treeArray[j]);
			j=j-2;
			}
		return treeArray[0];
	}
	private PairHeapNode[] doubleIfFull(PairHeapNode[] array, int index) {
		if (index == array.length) {
			PairHeapNode[] oldArray = array;
			array = new PairHeapNode[index * 2];
			for (int i = 0; i < index; i++)
				array[i] = oldArray[i];
		}
		return array;
	}
		
	public void insertNode(PairHeapNode node){
		size++;
		if(root==null)
			root=node;
		else
			root=compareAndLink(root,node);
	}
	private PairHeapNode compareAndLink(PairHeapNode first,PairHeapNode second){
		if(second==null) return first;
		if(first.getFrequency()>second.getFrequency()){
			second.prev=first.prev;
			first.prev=second;
			first.nextSibling=second.leftChild;
			if(first.nextSibling !=null)
				first.nextSibling.prev=first;
			second.leftChild=first;
			return second;
		}
		else{
			second.prev=first;
			first.nextSibling=second.nextSibling;
			if(first.nextSibling !=null)
				first.nextSibling.prev=first;
			second.nextSibling=first.leftChild;
			if(second.nextSibling !=null)
				second.nextSibling.prev=second;
			first.leftChild=second;
			return first;
		}
	}
	private boolean isEmpty(){
		return root==null;
	}
	public int getSize(){
		return size;
	}
}
}
class HuffmanBinaryHeap{
public void buildHuffmanTree(int[] data,int[] freq){
	MinHeap minheap=createandBuildMinHeap(data,freq);
	while(minheap.getSize()>1)
	{
		MinHeapNode left=minheap.extractMin();
		MinHeapNode right=minheap.extractMin();
		MinHeapNode root=new MinHeapNode(Integer.MAX_VALUE,left.getFrequency()
				+right.getFrequency());
		root.setLeft(left);
		root.setRight(right);
		minheap.insertNode(root);
	}
	MinHeapNode root=minheap.extractMin();
	//assignCodes(root,"");
	return;
}
public void assignCodes(MinHeapNode root,String code)
{ 
	if(root==null) return;
	if(root.left==null && root.right==null){
		root.code=code;
		System.out.println(root.index+": "+root.code);
		return;
	} 
	if(root.left!=null) assignCodes(root.left,code+'0');
	if(root.right!=null) assignCodes(root.right,code+'1');
}
public MinHeap createandBuildMinHeap(int[] data,int[] freq)
{
	MinHeapNode[] tree=new MinHeapNode[freq.length];
	for(int i=0;i<freq.length;i++)
	{
		tree[i]=new MinHeapNode(data[i],freq[i]);
		
	}
	MinHeap heap=new MinHeap(tree);
	return heap;
}
class MinHeapNode{
	private int index;
	private int frequency;
	private MinHeapNode left=null;
	private MinHeapNode right=null;
	private String code;
	public void setCode(String code){
		this.code=code;
	}
	public MinHeapNode(int index,int frequency){
		this.index=index;
		this.frequency=frequency;
	}
	public void setLeft(MinHeapNode left){
		this.left=left;
	}	
	public void setRight(MinHeapNode right){
		this.right=right;
	}
	public int getFrequency(){
		return frequency;
	}
}
class MinHeap {
	private MinHeapNode[] tempHeap;
	private int size;
	private int capacity;
	public MinHeap(MinHeapNode[] tree)
	{
		this.tempHeap=new MinHeapNode[tree.length+1];
		this.size=tree.length;
		capacity=size;
		for(int i=0;i<tree.length;i++)
			tempHeap[i+1]=tree[i];
		buildMinHeap(size);
	}

	public void buildMinHeap(int heapsize)
	{
		for(int i=heapsize/2;i>=1;i--)
			minHeapify(i);
	}
	public MinHeapNode extractMin()
	{
		MinHeapNode min=tempHeap[1];
		tempHeap[1]=tempHeap[size];
		tempHeap[size]=null;
		size--;
		minHeapify(1);
		return min;
	}
	public void insertNode(MinHeapNode node)
	{
		if(size<capacity)
		{
			size++;
			tempHeap[size]=node;
			int current=size;
			while (isValid(current)
					&& isValid(current/2)
					&& tempHeap[current/2].getFrequency() > tempHeap[current]
							.getFrequency()) {
				swap(current/2, current);
				current = current / 2;
			}
		}
	}
	public void minHeapify(int index)
	{
		int left=getLeft(index);
		int right=getRight(index);
		int smallest=left;
		if(isValid(right))
		{
			if(tempHeap[right].getFrequency()<tempHeap[left].getFrequency())
				smallest=right;
		}
		if(isValid(smallest) && tempHeap[smallest].getFrequency()<tempHeap[index].getFrequency())
		{
			swap(smallest,index);
			minHeapify(smallest);
		}
	}
	private void swap(int smallest,int index)
	{
		MinHeapNode temp=tempHeap[smallest];
		tempHeap[smallest]=tempHeap[index];
		tempHeap[index]=temp;
	}
	private boolean isValid(int index)
	{
		if(index>=1 && index<=size)
			return true;
		return false;
	}
	public int getSize(){
		return size;
	}
	public int getLeft(int i)
	{
		return 2*i;	
	}
	public int getRight(int i)
	{
		return 2*i+1;
	}
	public int getParent(int i)
	{
		return i/2;
	}
}
}








