package model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Observable;
import java.util.Observer;

import config.ConfRW;
import controller.Lexicon;
import controller.Word;

/*每当被监听的Word发生改变时调用，主要用来修改用户的user.conf文件*/

public class WordHandler implements Observer
{
	/*java的单例模式，确保整个应用程序中只有一个lexicon实例*/
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
		/*TO　BE　DONE　
		 * 根据Word修改txt文件，使用randomAccess.
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
			Word.getInstance().setEnglish(Word.getInstance().getEnglish());
			
			return true;
		}
		else if(type.equals("1"))
		{
			readerConf.seek(Lexicon.getInstance().getEntryLastWord());
			Word.getInstance().setWord(readerConf.readLong(), readerConf.readInt(), readerConf.readUTF(), readerConf.readUTF());
			Word.getInstance().setChinese(Word.getInstance().getChinese());
			Word.getInstance().setEnglish(Word.getInstance().getEnglish());
			
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
				Word.getInstance().setEnglish(tempEnglish);
			}
			else
			{
				readerConf.seek(Lexicon.getInstance().getEntryLastWord());
				Word.getInstance().setWord(readerConf.readLong(), readerConf.readInt(), readerConf.readUTF(), readerConf.readUTF());
				Word.getInstance().setChinese(tempChinese);
				Word.getInstance().setEnglish(tempEnglish);
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

	public String getLastWord() throws IOException 
	{
		// TODO Auto-generated method stub
		RandomAccessFile readerConf = ConfRW.getInstance().getConf();
		readerConf.seek(Lexicon.getInstance().getEntryLastWord()+12);
		return readerConf.readUTF();
	}
}
