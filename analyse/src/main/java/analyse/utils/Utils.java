package analyse.utils;

import java.util.List;

public final class Utils {

	/**
	 * 判断两个list里是否含有的元素是否全部相同，若任意列表中有null认为不相同
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean listEqual(List<?> list1, List<?> list2) {
		if (list1 == list2)
			return true;
		else if (list1 == null || list2 == null)
			return false;
		else if (list1.size() != list2.size())
			return false;
		else {
			for (Object object : list1) {
				if (object == null)
					return false;
				if (!list2.contains(object))
					return false;
			}
			return true;
		}
	}
	
}
