/*

HuffmanEncoder.java

Author: Ryan Moore 
Updated: 4-25-2018

This program can read a file, calculate the frequency of each character in the file,
place the characters and frequencies into a table indexed by character, create a Huffman Tree,
traverse the tree and print out the characters and frequencies,
read a file and encode the characters in the file using a Huffman tree, and
can decode a string of 1s and 0s using a HuffmanTree.

*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class HuffmanEncoder implements HuffmanCoding  {
    private int[] frequencies = new int[128];
    private String[] codes = new String[128];
    
    public String getFrequencies(File inputFile) throws FileNotFoundException   {
        int i = 0;
        StringBuilder charFreqTable = new StringBuilder();
        
        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);  

            int c = 0;
            while((c = br.read()) != -1) {		//reads a file a byte at a time and increments the characters frequency
                char character = (char) c;
                i = frequencies[c];			
                i++;
                frequencies[c] = i;                               
            }
        
        } catch (IOException e) {

        } 


        for (i = 32; i < 128; i++)  {			//creates our string, only adds characters that are in the text file
            if (frequencies[i] != 0)    {
                char c = (char)i;
                charFreqTable.append(c).append(" ").append(frequencies[i]).append("\n");
            } 
        }

        return charFreqTable.toString();
    }


    public HuffTree buildTree(File inputFile) throws FileNotFoundException, FileNotFoundException   {
        String freqTable = getFrequencies(inputFile);

        Comparator<HuffTree> comparator = new FrequencyComparator();						//orders the HuffTree objects based on their frequencies
        PriorityQueue<HuffTree> queue = new PriorityQueue<HuffTree>(128, comparator);

        for (int i = 32; i < 128; i++)  {
            if (frequencies[i] != 0)    {
                char c = (char)i;
                queue.add(new HuffTree(c, frequencies[i], null, null));			//adds the nodes to the priority queue
            }
        }

        HuffTree tmp1, tmp2, tmp3 = null;

        while (queue.size() > 1) { // While two items left
            tmp1 = queue.remove();
            tmp2 = queue.remove();
            tmp3 = new HuffTree('\0', tmp1.weight() + tmp2.weight(), tmp1.root(), tmp2.root());
            queue.add(tmp3);   // Return new tree to heap
        }

        return tmp3;				//returns the final HuffTree
    }

    public String encodeFile(File inputFile, HuffTree huffTree) throws FileNotFoundException	{
    	traverseHuffmanTree(huffTree);
    	String code = "";
    	StringBuilder bitStream = new StringBuilder();

    	try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);  

            int c = 0;
            while((c = br.read()) != -1) {			//reads the file a character at time
                char character = (char) c;
                code = codes[c];					//access the characters code
                bitStream.append(code);    			//and appends it to the string                          
            }
        
        } catch (IOException e) {

        }

        return bitStream.toString(); 

	}

	public String decodeFile(String code, HuffTree huffTree) throws Exception	{
		HuffNode h = huffTree.root();
		StringBuilder text = new StringBuilder();

		for (int i = 0; i < code.length(); i++)	{		//traveres through the tree until a character is found
			char c = code.charAt(i);
			if (c == '0')
				h = h.getLeft();
			if (c == '1')
				h = h.getRight();
			
			if (h.getElement() != '\0')	{
				text.append(h.getElement());
				h = huffTree.root();
			}
		}

		return text.toString();
	}

    public String traverseHuffmanTree(HuffTree huffTree) throws FileNotFoundException{
        int i;
        char c;
        String code = "";
        HuffNode h = huffTree.root();
        StringBuilder charCodeTable = new StringBuilder();

        traverse(h, code);
        
        for (i = 32; i < 128; i++)  {
            if (codes[i] != null)    {
                c = (char)i;
                charCodeTable.append(c).append(" ").append(codes[i]).append("\n");
            } 
        }

        return charCodeTable.toString();
    }

    void traverse(HuffNode h, String code)  {			//recursively traverses through the HuffTree so that every character has a code
        int i;
        char c;

        if (h.getLeft().getElement() != '\0')    {		//base case
                c = h.getLeft().getElement();
                i = (int)c;
                codes[i] = code + "0";
        }  

        if (h.getRight().getElement() != '\0')    {		//base case
                c = h.getRight().getElement();
                i = (int)c;
                codes[i] = code + "1";
        } 

        if (h.getLeft().getElement() == '\0')	{
            traverse(h.getLeft(), code + "0");
        } 

        if (h.getRight().getElement() == '\0')	{
        	traverse(h.getRight(), code + "1");
        }  
    }

}



class HuffNode  {
    private char element;
    private int weight;
    private HuffNode left;
    private HuffNode right; 

    HuffNode(char el, int wt, HuffNode l, HuffNode r)   {
        element = el;
        weight = wt;
        left = l;
        right = r;
    }

    char getElement()  {
        return element;
    }

    int getWeight() {
        return weight;
    } 

    HuffNode getLeft()  {
        return left;
    }  

    HuffNode getRight() {
        return right;
    } 
}




/** A Huffman coding tree */
class HuffTree  {
    private HuffNode root;  

    HuffTree(char el, int wt, HuffNode l, HuffNode r)   { 
        root = new HuffNode(el, wt, l, r); 
    }

    HuffNode root() { 
        return root; 
    }
  
    char element() {
        return root.getElement();
    }

    int weight()   { 
        return root.getWeight(); 
    }

}


class FrequencyComparator implements Comparator<HuffTree>   {

    public int compare(HuffTree x, HuffTree y)  {
       
        if (x.weight() < y.weight())  {
            return -1;
        }
        if (x.weight() > y.weight())  {
            return 1;
        }
        return 0;
    }
}