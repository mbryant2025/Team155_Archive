package org.usfirst.frc.team155.robot;

//import org.usfirst.frc.team155.robot.pixyCamVision.PixyException;
//import org.usfirst.frc.team155.robot.pixyCamVision.PixyPacket;
import org.usfirst.frc.team155.robot.PixyPacket;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class PixyCamBasic {

	// Warning: if the pixy is plugged in through mini usb, this code WILL NOT
	// WORK b/c the pixy is smart and detects where it should send data
	I2C pixy;
	Port port = Port.kOnboard;
	// PixyPacket[] packets; // prep up the array of PixyPacket - KB
	// PixyException pExc;
	String print;

	public PixyCamBasic() {
		pixy = new I2C(port, 0x54);
		// packets = new PixyPacket[7]; // fill array with 7 PixyPackets
		// pExc = new PixyException(print);
	}

	// This method parses raw data from the pixy into readable integers
	public int cvt(byte upper, byte lower) {
		return (((int) upper & 0xff) << 8) | ((int) lower & 0xff);
	}

	// This method gathers data, then parses that data, and assigns the ints to
	// global variables
	// sure would be nice if function protypes were followed..... KB
	// INPUT: -KB
	// -signature -KB
	// OUTPUT: -KB
	// returns a pixypacket on target #signature -KB
	public int readPacket(int Signature, PixyPacket[] packet) {
		// int Checksum;
		// int Sig;
		int i;
		int syncWord;

		byte[] rawData = new byte[100]; // set up an array of bytes, grab an
										// array of bytes from pixy -KB
		try {
			if (pixy.readOnly(rawData, 100)) {
			}
		} catch (RuntimeException e) {
		}

		if (rawData.length < 100) { // 32
			System.out.println("byte array length is broken");
			return -1;
		}
		// find a double sync word
		i = 0;
		while (true) {
			// System.out.println("searching for start frame, i: "+i);
			syncWord = cvt(rawData[i + 1], rawData[i + 0]); // read 2
															// consecutive bytes
			// System.out.println("syncWord is: " + (syncWord & 0xFFFFL));
			if (syncWord == 0xaa55) { // is this a syncword?
				/*
				 * System.out.println("1st syncword"+"i: " +i);
				 * System.out.println("checkSum= "+cvt(rawData[i + 5], rawData[i
				 * + 4])); System.out.println("signature = "+cvt(rawData[i + 7],
				 * rawData[i + 6])); System.out.println("X= "+cvt(rawData[i +
				 * 9], rawData[i + 8])); System.out.println("Y= "+cvt(rawData[i
				 * + 11], rawData[i + 10]));
				 * System.out.println("Width= "+cvt(rawData[i + 13], rawData[i +
				 * 12])); System.out.println("Height= "+cvt(rawData[i + 15],
				 * rawData[i + 14]));
				 */
				syncWord = cvt(rawData[i + 3], rawData[i + 2]); // read another
																// 2 consecutive
																// bytes
				if (syncWord == 0xaa55) { // is this a syncword?
					System.out.println("2nd syncword");
					break; // SUCCSESS
				}
			}
			i++;
			if (i == 80) // something went wrong, we found no syncword 80 is
							// arbitrary
				return -2;
		}

		System.out.println("found a double sync work at:" + i);

		// TEMPORARY WARNING: this may behave unexpectedly when there are lots
		// of zeros
		int block = 0;
		// System.out.println("packet array length is: "+ packet);
		// packet[0].X=0;
		while (true) {
			// populate the array with the first block
			if ((i + 15) > 99) // protection from being out of bounds during the
								// data retrieval
				return (block - 1);
			System.out.println("i: " + i + "  block: " + block);
			System.out.println("checkSum= "
					+ cvt(rawData[i + 5], rawData[i + 4]));
			System.out.println("signature = "
					+ cvt(rawData[i + 7], rawData[i + 6]));
			System.out.println("X= " + cvt(rawData[i + 9], rawData[i + 8]));
			System.out.println("Y= " + cvt(rawData[i + 11], rawData[i + 10]));
			System.out.println("Width= "
					+ cvt(rawData[i + 13], rawData[i + 12]));
			System.out.println("Height= "
					+ cvt(rawData[i + 15], rawData[i + 14]));
			/*
			 * packet[block].checkSum=cvt(rawData[i + 5], rawData[i + 4]);
			 * packet[block].signature = cvt(rawData[i + 7], rawData[i + 6]);
			 * packet[block].X=cvt(rawData[i + 9], rawData[i + 8]);
			 * packet[block].Y=cvt(rawData[i + 11], rawData[i + 10]);
			 * packet[block].Width=cvt(rawData[i + 13], rawData[i + 12]);
			 * packet[block].Height=cvt(rawData[i + 15], rawData[i + 14]); //
			 * Compute distance to object // Distance=Target(ft- 2 inches wide-
			 * 2/12)*FOV(pix - // 320)/(2*zarget(pix)*tangent(FOV 75degrees by
			 * 47 degrees)) packet[block].Distance = (int) (packet[block].Width
			 * / 12 320 / (2 * packet[block].X) * 3.732);
			 * 
			 * //check sum checking... //on failure, set checksum to -1 if
			 * (packet[block].checkSum != packet[block].signature +
			 * packet[block].X + packet[block].Y + packet[block].Width +
			 * packet[block].Height) { packet[block].checkSum = -1;
			 * 
			 * }
			 */

			// getting ready for the next block of data
			if ((i + 19) > 99) // we're near the end of the stream
				return block;
			syncWord = cvt(rawData[i + 17], rawData[i + 16]);
			if (syncWord == 0xaa55) { // verify the next one is sync word
				syncWord = cvt(rawData[i + 19], rawData[i + 18]);
				if (syncWord == 0xaa55) { // handles if we've gone too far
					return block; // we encountered 2 sync words, gone too far.
				} else { // not a sync word, so this must be another target form
							// the pixycam
					i = i + 16 - 2; // is this right?

					block++;
					if (block == 16) // hard limit of 16 objects
						return (block - 1);
				}

			} else {
				return block;

			}
		}

	}

}
