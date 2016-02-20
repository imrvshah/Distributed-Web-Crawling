import java.util.*;

public class Merge_Sort {

	public static void mergeSort(Word []number, int low, int high) {
		if (low < high) {
			int mid = (low+high)/2;
			mergeSort(number, low, mid);
			mergeSort(number, mid+1, high);
			merge(number, low, mid, high);
		}
	}
	
	private static void merge(Word []number, int low, int mid, int high) {
		Word []result = new Word[high-low+1];
		int i=low, j=mid+1, k=0;
		while (i<=mid && j<=high) {
			if (number[i].count < number[j].count) {
				result[k++] = number[i++];
			}
			else {
				result[k++] = number[j++];
			}
		}
		while (i<=mid) {
			result[k++] = number[i++];
		}
		while (j<=high) {
			result[k++] = number[j++];
		}
		for (i=low, k=0; i<=high; i++) {
			number[i]=result[k++];
		}
	}
}