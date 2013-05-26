package models;


import java.util.List;
import play.libs.F;
import play.libs.F.ArchivedEventStream;
import play.libs.F.EventStream;


public class Blackboard
{
	public final ArchivedEventStream<String> boardsvg = new ArchivedEventStream<String>(500);
	public final ArchivedEventStream<String> messages = new ArchivedEventStream<String>(500);
	
	
	public void addPath(String path)
	{
		boardsvg.publish(path);
	}
	
	
	public void say(String msg)
	{
		messages.publish(msg);
	}
	
	
	public List<String> archive()
	{
		return boardsvg.archive();
	}
	
	
	static Blackboard instance = null;
	
	
	public static Blackboard get()
	{
		if (instance == null)
		{
			instance = new Blackboard();
		}
		return instance;
	}
}
