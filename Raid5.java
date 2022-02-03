/* Name:         Ruiqi Wang
 * Email:        rwang26@lsu.edu
 * Project:      PA-2(RAID)
 * Instructor:   Feng Chen
 * Class:        cs4103-au21
 * Login ID:     cs4103an
 */
package raid5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Raid5 {

    private static String DiskFP = "disk."; //read input and create disk file
    private static String DiskSZ = "disk.z"; 

    public static void main(String[] args) throws IOException {
        raid5Write(4, 16, "sample-input2");  //split the original input into 4 files
        raid5Read(4, 16, "output"); //generate the each file and we can read them separately
        raid5Rebuild(4, 16, 0); // rebuild the file by these separate files
        raid5Read(4, 16, "output2"); //read the files except file0 as we missed
        raid5Write(4, 16, "sample-input.txt"); //read the input file and seperate them into 4 files
        raid5Read(4, 16, "output.txt"); //generate the file by all 4 separate files
        raid5Rebuild(4, 16, 0); //rebuild the files with the missing file 0
        raid5Read(4, 16, "output2.txt"); //rebuild the file by three files which miss file 0
        
    }

    //the method to make the file split into 4 seperate files individually
    private static void raid5Write(int disknum, int blocksize, String fname) throws IOException {
        byte bytes[] = fileToBytes(fname); //make the file into bytes
        byte disks[][] = createDisks(bytes.length, disknum, blocksize); //store them in each disk
        byte chksum[] = new byte[blocksize]; //seperate the blocksize
        int d = 0, i = 0, j = 0, b = 0, len = bytes.length;  //make each size equal the length of one byte
        //create each file as the length of blocksize one by one
        while(i <= len) {
            if ((d == disknum - 1) || (i == len)) {
                for(b = 0; b < blocksize; b++) disks[disknum - 1][j+b] = chksum[b];
                if(i != len) { d = 0; j += b; b = 0; } else i ++;
            } else {
                if(d == 0)  chksum[b] = bytes[i]; 
                else chksum[b] ^= bytes[i];
                disks[d][j+b] = bytes[i];
                b ++;
                if(b == blocksize) { d ++; b = 0; }
                i ++;
            }
        }
        writeDisks(disks);
        writeFileSize(bytes.length);
    }
    
    //the methof to read the files from input
    private static void raid5Read(int disknum, int blocksize, String fname) throws IOException {
        int filesize = readFileSize(); 
        byte bytes[] = new byte[filesize]; 
        byte disks[][] = createDisks(filesize, disknum, blocksize);
        readDisks(disks); //the method I create to read the data from file to data
        int i = 0, j = 0, d=0, b = 0;
        //read the information
        while(i < filesize) {
            bytes[i] = disks[d][j+b];
            b ++;
            if(b == blocksize) { d ++; b = 0;}
            if(d == disknum - 1) { d = 0; j += blocksize;}
            i ++; 
        }
        bytesToFile(fname, bytes);
    }
    
    //rebuild the file by separate files I created before
    private static void raid5Rebuild(int disknum, int blocksize, int baddisk) throws IOException {
        int filesize = readFileSize();
        byte disks[][] = createDisks(filesize, disknum, blocksize); //create each disk which contain parts of information each by each
        readDisks(disks); //the method I create to read the data from file to data
        int j = 0, d=0;
        byte chksum;
        for(j = 0; j < disks[0].length; j ++) {
            chksum =  0x00;
            for(d = 0; d < disks.length; d ++) {
                if(d != baddisk) chksum ^= disks[d][j];
            }
            disks[baddisk][j] = chksum;
        }
        bytesToFile(DiskFP + baddisk, disks[baddisk]);
    }

    //make the file into byte
    private static byte[] fileToBytes(String fname) throws IOException {
        return Files.readAllBytes(Paths.get(fname));        
    }

    //make the byte to file
    private static void bytesToFile(String fname, byte[] bytes)  throws IOException {
        Files.write(Paths.get(fname), bytes);
    }

    //write the size of raw file
    private static void writeFileSize(int size) throws IOException{
        Files.writeString(Paths.get(DiskSZ), String.valueOf(size));
    }

    //read the file by raw file size
    private static int readFileSize() throws IOException {
        return Integer.parseInt(Files.readString(Paths.get(DiskSZ)));    
    }

    //create the each disk which contain a part of information
    private static byte[][] createDisks(int size, int disknum, int blocksize) {
        int disksize = (size / blocksize / (disknum - 1) + 1) * blocksize;
        return new byte[disknum][disksize];
    }

    //write the disks from data to file
    private static void writeDisks(byte[][] disks) throws IOException {
        for(int d = 0; d < disks.length; d ++) 
            bytesToFile(DiskFP + d, disks[d]);
    }

    //read the data from file to data
    private static void readDisks(byte[][] disks) throws IOException {
        for(int d = 0; d < disks.length; d ++) {
            byte part[] = fileToBytes(DiskFP + d);
            for(int j = 0; j < part.length; j ++) disks[d][j] = part[j];
        }
    }
    
}
