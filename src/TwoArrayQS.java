/**
 * This class implements a standard quick sort, only all array swaps are duplicated over
 * a second array (for instance, containing the original indices of the first.
 */
public class TwoArrayQS {    
	/**
     * Performs a quicksort on arr1, duplicating array swaps over arr2.
     */
    public static void quicksort(int[] arr1, int[] arr2, int left, int right) {
        if(right > left) {
            int pivotIndex = left;
            pivotIndex = partition(arr1, arr2, left, right, pivotIndex);
            quicksort(arr1, arr2, left, pivotIndex-1);
            quicksort(arr1, arr2, pivotIndex+1, right);
        }
    }     
     
    private static int partition(int[] arr1, int[] arr2, int left, int right, int pivotIndex) {
        int pivotValue = arr1[pivotIndex];
        arr1[pivotIndex] = arr1[right];
        arr1[right] = pivotValue;
        int t = arr2[pivotIndex];
        arr2[pivotIndex] = arr2[right];
        arr2[right] = t;
        int storeIndex = left;
        for(int i = left; i < right; i++) {
            if(arr1[i] <= pivotValue) {
                t = arr1[i];
                arr1[i] = arr1[storeIndex];
                arr1[storeIndex] = t;
                t = arr2[i];
                arr2[i] = arr2[storeIndex];
                arr2[storeIndex] = t;
                storeIndex++;
            }
        }
        t = arr1[storeIndex];
        arr1[storeIndex] = arr1[right];
        arr1[right] = t;
        t = arr2[storeIndex];
        arr2[storeIndex] = arr2[right];
        arr2[right] = t;        
        return storeIndex;
    }
}
