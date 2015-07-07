package net.wyxj.broadcast;

/**
 * �����������������
 * 
 * @author ��
 * 
 */
public interface HostListener {

	/**
	 * ����������Ϣ����������
	 * 
	 * @param info
	 * @param reason
	 */
	public void onAdd(HostInfo info, Reason reason);

	/**
	 * ����������Ϣ����������
	 * 
	 * @param info
	 * @param reason
	 */
	public void onDelete(HostInfo info, Reason reason);

	/**
	 * 
	 * @param info
	 * @param type
	 */
	public void onDefault(HostInfo info, Message message);

}
