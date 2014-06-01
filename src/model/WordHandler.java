package model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Observable;
import java.util.Observer;

import config.ConfRW;
import controller.Lexicon;
import controller.Word;

/*ÿ����������Word�����ı�ʱ���ã���Ҫ�����޸��û���user.conf�ļ�*/

public class WordHandler implements Observer
{
	/*java�ĵ���ģʽ��ȷ������Ӧ�ó�����ֻ��һ��lexiconʵ��*/
	private volatile static WordHandler instance = null; 

	  public static WordHandler getInstance() { 
	    if (instance == null) { 
	      synchronized (WordHandler.class) { 
	        if(instance == null) { 
	          instance = new WordHandler(); 
	        } 
	      } 
	    } 
	    return instance; 
	} 
	  
	public void update(Observable word, Object arg1) 
	{
		// TODO Auto-generated method stub
		/*TO��BE��DONE��
		 * ����Word�޸�txt�ļ���ʹ��randomAccess.
		 * */
		if(word instanceof Word)
		{    
			if(arg1.equals("state"))
			{
				try {
					RandomAccessFile readerConf = ConfRW.getInstance().getConf();
					readerConf.seek(((Word) word).getEntry()+8);
					readerConf.writeInt(((Word) word).getState());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        } 
	}
	
	public boolean chooseWord(String type) throws IOException
	{
		RandomAccessFile readerConf = ConfRW.getInstance().getConf();
		
		if(type.equals("0"))
		{
			readerConf.seek(Lexicon.getInstance().getEntryWord());
			Word.getInstance().setWord(readerConf.readLong(), readerConf.readInt(), readerConf.readUTF(), readerConf.readUTF());
			Word.getInstance().setChinese(Word.getInstance().getChinese());
			
			return true;
		}
		else if(type.equals("1"))
		{
			readerConf.seek(Lexicon.getInstance().getEntryLastWord());
			Word.getInstance().setWord(readerConf.readLong(), readerConf.readInt(), readerConf.readUTF(), readerConf.readUTF());
			Word.getInstance().setChinese(Word.getInstance().getChinese());
			
			return true;
		}
		
		else if(type.equals("3"))
		{
			readerConf.seek(Word.getInstance().getEntry());
			readerConf.skipBytes(12);
			readerConf.readUTF();
			readerConf.readUTF();
			
			long tempEntry = readerConf.readLong();
			int tempState = readerConf.readInt();
			String tempEnglish = readerConf.readUTF();
			String tempChinese = readerConf.readUTF();
			
			if(tempEnglish.startsWith(Lexicon.getInstance().getType()))
			{
				Word.getInstance().setWord(tempEntry, tempState, tempEnglish, tempChinese);
				Word.getInstance().setChinese(tempChinese);
			}
			else
			{
				readerConf.seek(Lexicon.getInstance().getEntryLastWord());
				Word.getInstance().setWord(readerConf.readLong(), readerConf.readInt(), readerConf.readUTF(), readerConf.readUTF());
				Word.getInstance().setChinese(Word.getInstance().getChinese());
			}
			
			return true;
		}
		
		else
		{
			readerConf.seek(Lexicon.getInstance().getEntryWord());
			int count = Lexicon.getInstance().getCountTotal();
			while(count>0)
			{
				long tempEntry = readerConf.readLong();
				int tempState = readerConf.readInt();
				String tempEnglish = readerConf.readUTF();
				String tempChinses  = readerConf.readUTF();
				
				if(tempEnglish.startsWith(type))
				{
					Word.getInstance().setWord(tempEntry, tempState, tempEnglish, tempChinses);
					Word.getInstance().setChinese(tempChinses);
					return true;
				}
				
				count--;
			}
		}
		return false;
	}
}