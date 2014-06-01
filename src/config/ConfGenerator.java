package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ConfGenerator 
{
	@SuppressWarnings("rawtypes")
	public void generateConfig() throws IOException
	{
		File confFile = new File("user.conf");
			
		if(!confFile.exists())
		{
			confFile.createNewFile();
		}
		
		BufferedReader readerDict = new BufferedReader(new FileReader("dictionary.txt"));
		RandomAccessFile readerConf = new RandomAccessFile(confFile,"rw");
		
		String lineTemp="";
	
		//��ȡdictionary.txt��Ϊ���ôʿ���׼��
		Map<String,Integer> lexiconConf = new HashMap<String,Integer>(); 
		Map<String,Long> lexiconTemp = new HashMap<String,Long>(); 
		
		String type = "";
		int count = 0;
		
		while((lineTemp = readerDict.readLine())!=null)
		{
			type = lineTemp.substring(0, 1);
			count = lexiconConf.containsKey(type)?count++:1;
			
			lexiconConf.put(type,count);
		} 
		readerDict.close();
		
		
		readerConf.seek(0);
		
		//д�ʿ�ʹ���������Ϣ�ķָ��
		readerConf.writeLong(0);
		//д��ʿ���Ϣ
		Iterator iter = lexiconConf.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			
			//�ʿ��������
			readerConf.writeLong(readerConf.getFilePointer());
			//�ʿ����
			readerConf.writeUTF((String) key);
			//�ʿ⺬�еĵ��ʸ���
			readerConf.writeInt((Integer) val);
			//�ʿ����
			lexiconTemp.put((String) key,readerConf.getFilePointer());
			readerConf.writeLong(0);
			//�ʿ��ϴα������ʵ����
			readerConf.writeLong(0);
			//�ʿ��Ѿ����ĵ��ʸ���
			readerConf.writeInt(0);
			//�ʿⱳ�Եĵ��ʵĸ���
			readerConf.writeInt(0);
			
		}
		
		long tempPoint = readerConf.getFilePointer(); 
		readerConf.seek(0);
		readerConf.writeLong(tempPoint);
		
		//��ʼд������Ϣ
		readerConf.seek(tempPoint);
		readerDict = new BufferedReader(new FileReader("dictionary.txt"));
		while((lineTemp = readerDict.readLine())!=null)
		{
			String[] info = lineTemp.split("   ");
			
			if(info.length!=2)
			{
				continue;
			}
			
			if(lexiconTemp.containsKey(info[0].substring(0,1)))
			{
				tempPoint = readerConf.getFilePointer();
				readerConf.seek(lexiconTemp.get(info[0].substring(0,1)));
				//�ʿ�д�����
				readerConf.writeLong(tempPoint);
				readerConf.writeLong(tempPoint);
				
				//���ص�ǰ����
				readerConf.seek(tempPoint);
				lexiconTemp.keySet().remove(info[0].substring(0,1));
			}
			
			//д��entry
			readerConf.writeLong(readerConf.getFilePointer());
			//д��״̬
			readerConf.writeInt(0);
			//д����������
			readerConf.writeUTF(info[0]);
			//д��Ӣ������
			readerConf.writeUTF(info[1]);
		} 
		
		/*test
		readerConf.seek(0);
		long temp = readerConf.readLong();
		while(readerConf.getFilePointer()<temp)
		{
			System.out.print(readerConf.readLong()+"-");
			System.out.print(readerConf.readUTF()+"-");
			System.out.print(readerConf.readInt()+"-");
			System.out.print(readerConf.readLong()+"-");
			System.out.print(readerConf.readLong()+"-");
			System.out.print(readerConf.readInt()+"-");
			System.out.println(readerConf.readInt());
		}
		while(readerConf.getFilePointer()<readerConf.length())
		{
			System.out.print(readerConf.readLong()+"-");
			System.out.print(readerConf.readUTF()+"-");
			System.out.print(readerConf.readUTF()+"-");
			System.out.println(readerConf.readInt());
		}
		*/
		readerConf.close();
		readerDict.close();
	}
}