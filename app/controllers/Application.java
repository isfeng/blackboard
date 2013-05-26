package controllers;


import static play.mvc.Http.WebSocketEvent.SocketClosed;
import static play.mvc.Http.WebSocketEvent.TextFrame;
import play.*;
import play.libs.F;
import play.libs.F.Either;
import play.libs.F.EventStream;
import play.libs.F.Promise;
import play.mvc.*;
import play.mvc.Http.WebSocketClose;
import play.mvc.Http.WebSocketEvent;
import java.util.*;
import com.sun.org.apache.bcel.internal.generic.ClassObserver;
import models.*;
import static play.libs.F.*;
import static play.libs.F.Matcher.*;
import static play.mvc.Http.WebSocketEvent.*;


public class Application extends Controller
{
	
	public static void index()
	{
		render();
	}
	
	
	public static void dispatch(String page) throws Exception
	{
		StringBuffer buf = new StringBuffer("/Application/");
		buf.append(page).append(".html");
		render(buf.toString());
	}
	
	
	public static class WebSocket extends WebSocketController
	{
		
		public static void listen()
		{
			while (inbound.isOpen())
			{
				String event = await(StatefulModel.instance.event.nextEvent());
				outbound.send(event);
			}
		}
		
		
		public static void draw()
		{
			
			Blackboard board = Blackboard.get();
			
			EventStream<String> boardEvent = board.boardsvg.eventStream();
			EventStream<String> msgEvent = board.messages.eventStream();
			
			while (inbound.isOpen())
			{
				F.E3<WebSocketEvent, String, String> e = await(Promise.waitEither(inbound.nextEvent(), boardEvent.nextEvent(), msgEvent.nextEvent()));
				
				for (String path : TextFrame.match(e._1))
				{
					if (path.equals("clear"))
						board.addPath(path);
					else if (path.startsWith("say"))
						board.say(path);
					else
						board.addPath(path);
				}
				
				for (String path : ClassOf(String.class).match(e._2))
				{
					outbound.send(path);
				}
				
				for (String msg : ClassOf(String.class).match(e._3))
				{
					outbound.send(msg);
				}
				
				for (WebSocketClose closed : SocketClosed.match(e._1))
				{
					disconnect();
				}
				
				
			}
		}
	}
}