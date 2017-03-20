import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RemoveDuplicates {

    /**
     * Odstrani jednu polozku z pole
     * @param data vstupni pole dat
     * @param index index polozky ktera se ma odstranit
     * @return nove pole bez jedne polozky
     */
    public static int[] removeItem(int[] data, int index){
        int[] result = new int[data.length-1];
        for (int i = 0;i<index;i++)
            result[i] = data[i];
        for (int i = index + 1;i<data.length;i++)
            result[i-1] = data[i];
        return result;
    }

    /**
     * Prochazi vsechny polozky a odstranuje potencialni duplikaty metodou removeItem
     * @param data vstupni pole
     * @return data s odstranenymi duplikaty
     */
    static int[] removeDuplicates1(int[] data){
        int[] result = data;
        for (int i = 0;i<result.length;i++){
            for (int j = i+1;j<result.length;j++){
                if (result[i] == result[j])
                    result = removeItem(result, j);
            }
        }
        return result;
    }

    /**
     * Prochazi vsechny polozky a provadi ostraneni vsech duplikatu jedne polozky najednou
     * @param data vstupni pole
     * @return data s odstranenymi duplikaty
     */
    static int[] removeDuplicates2(int[] data){
        int totalCount = 0;
        int[] result = data;
        for(int i = 0;i<result.length;i++){
            int count = 0; // pocet duplikatu
            for (int j = i+1;j<result.length;j++)
                if (result[j] == result[i])
                    count++;
            totalCount += count;
            if (count>0){
                // odstraneni vsech duplikatu polozky result[i]
                int[] newResult = new int[result.length-count];
                for (int k = 0;k<=i;k++)
                    newResult[k] = result[k];
                int index = i + 1; // index v cilovem poli
                for (int k = i+1;k<result.length;k++){
                    if (result[k]!=result[i]){
                        // neni duplikat
                        newResult[index] = result[k];
                        index++;
                    }
                }
                result = newResult;
            }
        }
        return result;
    }

    /**
     * Pouziva redukci pomoci pole zaznamu, zda dane cislo bylo nalezeno v datech ci nikoli
     * @param data vstupni pole
     * @return data s odstranenymi duplikaty
     */
    static int[] removeDuplicates3(int[] data){
        boolean[] encountered = new boolean[1000000];
        int count = 0; // pocet unikatnich cisel
        for (int i = 0;i<data.length;i++){
            if (!encountered[data[i]]){
                encountered[data[i]] = true;
                count++;
            }
        }
        encountered = new boolean[1000000];
        int[] result = new int[count];
        int index = 0;
        for (int i = 0;i<data.length;i++){
            if (!encountered[data[i]]){
                result[index] = data[i];
                encountered[data[i]] = true;
                index++;
            }
        }
        return result;
    }

    /**
     * generuje nahodna data v rozsahu do 10 000,
     * cimz se simuluje, ze cca 90% cisel je "neaktivnich"
     * @param count pocet pozadovanych cisel
     * @return pole nahodnych cisel
     */
    static int[] generateData(int count)
    {
        int[] result = new int[count];
        Random r = new Random();
        for (int i = 0;i<result.length;i++)
            result[i] = r.nextInt(100000);
        return result;
    }

    public static void main(String[] args)
    {
        int count = 30000;
        int[] data = generateData(count);

        int[] reducedData1 = removeDuplicates1(data);
        if(check(reducedData1)){
            System.out.println("All good.");
        }
        int[] reducedData2 = removeDuplicates2(data);
        if(check(reducedData2)) {
            System.out.println("All good.");
        }
        int[] reducedData3 = removeDuplicates3(data);
        if(check(reducedData3)){
            System.out.println("All good.");
        }
        System.out.println("All done.");
    }

    public static boolean check(int[] data){
        Set<Integer> set = new HashSet<Integer>();

        for(int i : data){
            if(!set.add(i)){
                return false;
            }
        }
        if(data.length == set.size())
            return true;
        return false;
    }
}