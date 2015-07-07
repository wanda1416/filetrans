package net.wyxj.broadcast;

/**
 * 主机在线情况监听器
 * @author 辉
 *
 */
public interface HostListener {
	
	public void onAdd(HostInfo info);
	
	public void onDelete(HostInfo info);
	
}
