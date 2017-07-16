package app.user.dao;
/**
 * 各种消息类型
 * @author victor
 *
 */
public class Message {

	private long id;
	private long userId;
	private int msgType;
	private int msgUserType;
	public int getMsgUserType() {
		return msgUserType;
	}
	public void setMsgUserType(int msgUserType) {
		this.msgUserType = msgUserType;
	}
	private String addTime;
	private String content;
	private String expirationTime;
	private int msgStatus;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	public int getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}
	
	public String toString(){
		return String.format("id:"+this.getId()+", "
							 +"用户Id:"+this.getUserId()+", "
							 +"消息类型:"+this.getMsgType()+", "
							 +"用户类型:"+this.getMsgUserType()+", "
							 +"文字信息:"+this.getContent()+", "
							 +"添加时间:"+this.getAddTime()+", "
							 +"更新时间:"+this.getExpirationTime()+", "
							 +"消息状态:"+this.getMsgStatus());
	}
	
}
