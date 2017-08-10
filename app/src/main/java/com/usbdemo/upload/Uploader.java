package com.usbdemo.upload;

import java.util.ArrayList;
import java.util.List;

/**
 *Uploads a firmware file to the PX FMU bootloader
 * Created by SJJ on 2017/7/11.
 */

public class Uploader {
//  protocol bytes
    public final static byte INSYNC          = 0x12;
    public final static byte EOC             = 0x20;
//  reply bytes
    public final static byte OK              = 0x10;
    public final static byte FAILED          =0x11;
    public final static byte INVALID         = 0x13;    // rev3+
    public final static byte BAD_SILICON_REV = 0x14;     // rev5+

//  command bytes
    public final static byte NOP             = 0x00;     // guaranteed to be discarded by the bootloader
    public final static byte GET_SYNC        = 0x21;
    public final static byte GET_DEVICE      = 0x22;
    public final static byte CHIP_ERASE      = 0x23;
    public final static byte CHIP_VERIFY     = 0x24;     // rev2 only
    public final static byte PROG_MULTI      = 0x27;
    public final static byte READ_MULTI      = 0x28;     // rev2 only
    public final static byte GET_CRC         = 0x39;    // rev3+
    public final static byte GET_OTP         = 0x2a;     // rev4+  , get a word from OTP area
    public final static byte GET_SN          = 0x2b;     // rev4+  , get a word from SN area
    public final static byte GET_CHIP        = 0x2c;     // rev5+  , get chip version
    public final static byte        SET_BOOT_DELAY  = 0x2d;     // rev5+  , set boot delay
    public final static byte        GET_CHIP_DES    = 0x2e;     // rev5+  , get chip description in ASCII
    public final static byte        MAX_DES_LENGTH  = 20;

    public final static byte REBOOT          = 0x30;
    public final static byte SET_BAUD        = 0x33;     // set baud

    public final static byte INFO_BL_REV     = 0x01;        // bootloader protocol revision
    public final static byte        BL_REV_MIN      = 2;             // minimum supported bootloader protocol
    public final static byte  BL_REV_MAX      = 5      ;        // maximum supported bootloader protocol
    public final static byte INFO_BOARD_ID   = 0x02;       // board type
    public final static byte INFO_BOARD_REV  = 0x03;        // board revision
    public final static byte INFO_FLASH_SIZE = 0x04;        // max firmware size in bytes

    public final static int  PROG_MULTI_MAX  = 252     ;       // protocol max is 255, must be multiple of 4
    public final static int READ_MULTI_MAX  = 252     ;     // protocol max is 255

    public final static byte[] NSH_INIT        = {0x0d,0x0d,0x0d};
    public final static String  NSH_REBOOT_BL   = "reboot -b\n";
    public final static String NSH_REBOOT      = "reboot\n";
    public final static byte[] MAVLINK_REBOOT_ID1 = {(byte) 0xfe,0x21,0x72, (byte) 0xff,0x00,0x4c,0x00,0x00,0x40,0x40,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xf6,0x00,0x01,0x00,0x00,0x53,0x6b};
    public final static byte[] MAVLINK_REBOOT_ID0 = {(byte) 0xfe,0x21,0x45, (byte) 0xff,0x00,0x4c,0x00,0x00,0x40,0x40,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) 0xf6,0x00,0x00,0x00,0x00, (byte) 0xcc,0x37};
    private final Connection connection;
    private List<Byte> otp = new ArrayList<>();
    private List<Byte> sn = new ArrayList<>();
    public Uploader(Connection connection) {
//         open the port, keep the default timeout short so we can poll quickly
        this.connection = connection;
    }

//    def __recv(self, count=1):
//    c = self.port.read(count)
//            if len(c) < 1:
//    raise RuntimeError("timeout waiting for data (%u bytes)" % count)
//        // print("recv " + binascii.hexlify(c))
//            return c
//
//    def __recv_int(self):
//    raw = self.__recv(4)
//    val = struct.unpack("<I", raw)
//            return val[0]
//
//    def __getSync(self):
//            self.port.flush()
//    c = bytes(self.__recv())
//            if c != self.INSYNC:
//    raise RuntimeError("unexpected %s instead of INSYNC" % c)
//    c = self.__recv()
//            if c == self.INVALID:
//    raise RuntimeError("bootloader reports INVALID OPERATION")
//        if c == self.FAILED:
//    raise RuntimeError("bootloader reports OPERATION FAILED")
//        if c != self.OK:
//    raise RuntimeError("unexpected response 0x%x instead of OK" % ord(c))
//
//            // attempt to get back into sync with the bootloader
//    def __sync(self):
//            // send a stream of ignored bytes longer than the longest possible conversation
//        // that we might still have in progress
//        // self.__send(uploader.NOP * (uploader.PROG_MULTI_MAX + 2))
//            self.port.flushInput()
//            self.__send(uploader.GET_SYNC +
//    uploader.EOC)
//            self.__getSync()
//
//    def __trySync(self):
//            try:
//            self.port.flush()
//            if (self.__recv() != self.INSYNC):
//            // print("unexpected 0x%x instead of INSYNC" % ord(c))
//            return False
//            c = self.__recv()
//            if (c == self.BAD_SILICON_REV):
//    raise NotImplementedError()
//            if (c != self.OK):
//            // print("unexpected 0x%x instead of OK" % ord(c))
//            return False
//            return True
//
//    except NotImplementedError:
//    raise RuntimeError("Programing not supported for this version of silicon!\n"
//                               "See https://pixhawk.org/help/errata")
//    except RuntimeError:
//            // timeout, no response yet
//            return False
//
//    // send the GET_DEVICE command and wait for an info parameter
//    def __getInfo(self, param):
//            self.__send(uploader.GET_DEVICE + param + uploader.EOC)
//    value = self.__recv_int()
//            self.__getSync()
//            return value
//
//    // send the GET_OTP command and wait for an info parameter
//    def __getOTP(self, param):
//    t = struct.pack("I", param)  // int param as 32bit ( 4 byte ) char array.
//            self.__send(uploader.GET_OTP + t + uploader.EOC)
//    value = self.__recv(4)
//            self.__getSync()
//            return value
//
//    // send the GET_SN command and wait for an info parameter
//    def __getSN(self, param):
//    t = struct.pack("I", param)  // int param as 32bit ( 4 byte ) char array.
//            self.__send(uploader.GET_SN + t + uploader.EOC)
//    value = self.__recv(4)
//            self.__getSync()
//            return value
//
//    // send the GET_CHIP command
//    def __getCHIP(self):
//            self.__send(uploader.GET_CHIP + uploader.EOC)
//    value = self.__recv_int()
//            self.__getSync()
//            return value
//
//    // send the GET_CHIP command
//    def __getCHIPDes(self):
//            self.__send(uploader.GET_CHIP_DES + uploader.EOC)
//    length = self.__recv_int()
//    value = self.__recv(length)
//            self.__getSync()
//    peices = value.split(",")
//            return peices
//
//    def __drawProgressBar(self, label, progress, maxVal):
//            if maxVal < progress:
//    progress = maxVal
//
//            percent = (float(progress) / float(maxVal)) * 100.0
//
//            sys.stdout.write("\r%s: [%-20s] %.1f%%" % (label, '='*int(percent/5.0), percent))
//            sys.stdout.flush()
//
//            // send the CHIP_ERASE command and wait for the bootloader to become ready
//    def __erase(self, label):
//    print("\n", end='')
//        self.__send(uploader.CHIP_ERASE +
//    uploader.EOC)
//
//            // erase is very slow, give it 20s
//            deadline = time.time() + 20.0
//        while time.time() < deadline:
//
//            // Draw progress bar (erase usually takes about 9 seconds to complete)
//    estimatedTimeRemaining = deadline-time.time()
//            if estimatedTimeRemaining >= 9.0:
//            self.__drawProgressBar(label, 20.0-estimatedTimeRemaining, 9.0)
//            else:
//            self.__drawProgressBar(label, 10.0, 10.0)
//            sys.stdout.write(" (timeout: %d seconds) " % int(deadline-time.time()))
//            sys.stdout.flush()
//
//            if self.__trySync():
//            self.__drawProgressBar(label, 10.0, 10.0)
//            return
//
//    raise RuntimeError("timed out waiting for erase")
//
//    // send a PROG_MULTI command to write a collection of bytes
//    def __program_multi(self, data):
//
//            if runningPython3:
//    length = len(data).to_bytes(1, byteorder='big')
//        else:
//    length = chr(len(data))
//
//            self.__send(uploader.PROG_MULTI)
//            self.__send(length)
//            self.__send(data)
//            self.__send(uploader.EOC)
//            self.__getSync()
//
//            // verify multiple bytes in flash
//    def __verify_multi(self, data):
//
//            if runningPython3:
//    length = len(data).to_bytes(1, byteorder='big')
//        else:
//    length = chr(len(data))
//
//            self.__send(uploader.READ_MULTI)
//            self.__send(length)
//            self.__send(uploader.EOC)
//            self.port.flush()
//    programmed = self.__recv(len(data))
//            if programmed != data:
//    print("got    " + binascii.hexlify(programmed))
//    print("expect " + binascii.hexlify(data))
//            return False
//        self.__getSync()
//                return True
//
//    // send the reboot command
//    def __reboot(self):
//            self.__send(uploader.REBOOT +
//    uploader.EOC)
//            self.port.flush()
//
//            // v3+ can report failure if the first word flash fails
//        if self.bl_rev >= 3:
//            self.__getSync()
//
//            // split a sequence into a list of size-constrained pieces
//    def __split_len(self, seq, length):
//            return [seq[i:i+length] for i in range(0, len(seq), length)]
//
//            // upload code
//    def __program(self, label, fw):
//    print("\n", end='')
//    code = fw.image
//            groups = self.__split_len(code, uploader.PROG_MULTI_MAX)
//
//    uploadProgress = 0
//            for bytes in groups:
//            self.__program_multi(bytes)
//
//            // Print upload progress (throttled, so it does not delay upload progress)
//    uploadProgress += 1
//            if uploadProgress % 256 == 0:
//            self.__drawProgressBar(label, uploadProgress, len(groups))
//            self.__drawProgressBar(label, 100, 100)
//
//            // verify code
//    def __verify_v2(self, label, fw):
//    print("\n", end='')
//        self.__send(uploader.CHIP_VERIFY +
//    uploader.EOC)
//            self.__getSync()
//    code = fw.image
//            groups = self.__split_len(code, uploader.READ_MULTI_MAX)
//    verifyProgress = 0
//            for bytes in groups:
//    verifyProgress += 1
//            if verifyProgress % 256 == 0:
//            self.__drawProgressBar(label, verifyProgress, len(groups))
//            if (not self.__verify_multi(bytes)):
//    raise RuntimeError("Verification failed")
//        self.__drawProgressBar(label, 100, 100)
//
//    def __verify_v3(self, label, fw):
//    print("\n", end='')
//        self.__drawProgressBar(label, 1, 100)
//    expect_crc = fw.crc(self.fw_maxsize)
//            self.__send(uploader.GET_CRC +
//    uploader.EOC)
//    report_crc = self.__recv_int()
//            self.__getSync()
//            if report_crc != expect_crc:
//    print("Expected 0x%x" % expect_crc)
//    print("Got      0x%x" % report_crc)
//    raise RuntimeError("Program CRC failed")
//        self.__drawProgressBar(label, 100, 100)
//
//    def __set_boot_delay(self, boot_delay):
//            self.__send(uploader.SET_BOOT_DELAY +
//            struct.pack("b", boot_delay) +
//    uploader.EOC)
//            self.__getSync()
//
//    def __setbaud(self, baud):
//            self.__send(uploader.SET_BAUD +
//            struct.pack("I", baud) +
//    uploader.EOC)
//            self.__getSync()
//
//            // get basic data about the board
//    def identify(self):
//            // make sure we are in sync before starting
//        self.__sync()
//
//                // get the bootloader protocol ID first
//    self.bl_rev = self.__getInfo(uploader.INFO_BL_REV)
//            if (self.bl_rev < uploader.BL_REV_MIN) or (self.bl_rev > uploader.BL_REV_MAX):
//    print("Unsupported bootloader protocol %d" % uploader.INFO_BL_REV)
//    raise RuntimeError("Bootloader protocol mismatch")
//
//    self.board_type = self.__getInfo(uploader.INFO_BOARD_ID)
//    self.board_rev = self.__getInfo(uploader.INFO_BOARD_REV)
//    self.fw_maxsize = self.__getInfo(uploader.INFO_FLASH_SIZE)
//
//            // upload the firmware
//    def upload(self, fw, force=False, boot_delay=None):
//            // Make sure we are doing the right thing
//        if self.board_type != fw.property('board_id'):
//    msg = "Firmware not suitable for this board (board_type=%u board_id=%u)" % (
//    self.board_type, fw.property('board_id'))
//    print("WARNING: %s" % msg)
//            if force:
//    print("FORCED WRITE, FLASHING ANYWAY!")
//            else:
//    raise IOError(msg)
//        if self.fw_maxsize < fw.property('image_size'):
//    raise RuntimeError("Firmware image is too large for this board")
//
//        // OTP added in v4:
//            if self.bl_rev > 3:
//            for byte in range(0, 32*6, 4):
//    x = self.__getOTP(byte)
//    self.otp = self.otp + x
//    print(binascii.hexlify(x).decode('Latin-1') + ' ', end='')
//            // see src/modules/systemlib/otp.h in px4 code:
//    self.otp_id = self.otp[0:4]
//    self.otp_idtype = self.otp[4:5]
//    self.otp_vid = self.otp[8:4:-1]
//    self.otp_pid = self.otp[12:8:-1]
//    self.otp_coa = self.otp[32:160]
//            // show user:
//            try:
//    print("type: " + self.otp_id.decode('Latin-1'))
//    print("idtype: " + binascii.b2a_qp(self.otp_idtype).decode('Latin-1'))
//    print("vid: " + binascii.hexlify(self.otp_vid).decode('Latin-1'))
//    print("pid: " + binascii.hexlify(self.otp_pid).decode('Latin-1'))
//    print("coa: " + binascii.b2a_base64(self.otp_coa).decode('Latin-1'))
//    print("sn: ", end='')
//                for byte in range(0, 12, 4):
//    x = self.__getSN(byte)
//    x = x[::-1]  // reverse the bytes
//    self.sn = self.sn + x
//    print(binascii.hexlify(x).decode('Latin-1'), end='')  // show user
//    print('')
//    print("chip: %08x" % self.__getCHIP())
//            if (self.bl_rev >= 5):
//    des = self.__getCHIPDes()
//            if (len(des) == 2):
//    print("family: %s" % des[0])
//    print("revision: %s" % des[1])
//    print("flash %d" % self.fw_maxsize)
//    except Exception:
//            // ignore bad character encodings
//    pass
//
//        if self.baudrate_bootloader_flash != self.baudrate_bootloader:
//    print("Setting baudrate to %u" % self.baudrate_bootloader_flash)
//            self.__setbaud(self.baudrate_bootloader_flash)
//    self.port.baudrate = self.baudrate_bootloader_flash
//            self.__sync()
//
//                    self.__erase("Erase  ")
//                    self.__program("Program", fw)
//
//            if self.bl_rev == 2:
//            self.__verify_v2("Verify ", fw)
//            else:
//            self.__verify_v3("Verify ", fw)
//
//            if boot_delay is not None:
//            self.__set_boot_delay(boot_delay)
//
//    print("\nRebooting.\n")
//        self.__reboot()
//                self.port.close()
//
//    def __next_baud_flightstack(self):
//    self.baudrate_flightstack_idx = self.baudrate_flightstack_idx + 1
//            if self.baudrate_flightstack_idx >= len(self.baudrate_flightstack):
//            return False
//
//    self.port.baudrate = self.baudrate_flightstack[self.baudrate_flightstack_idx]
//
//            return True
//
//    def send_reboot(self):
//            if (not self.__next_baud_flightstack()):
//            return False
//
//    print("Attempting reboot on %s with baudrate=%d..." % (self.port.port, self.port.baudrate), file=sys.stderr)
//    print("If the board does not respond, unplug and re-plug the USB connector.", file=sys.stderr)
//
//        try:
//                // try MAVLINK command first
//            self.port.flush()
//                    self.__send(uploader.MAVLINK_REBOOT_ID1)
//            self.__send(uploader.MAVLINK_REBOOT_ID0)
//            // then try reboot via NSH
//            self.__send(uploader.NSH_INIT)
//            self.__send(uploader.NSH_REBOOT_BL)
//            self.__send(uploader.NSH_INIT)
//            self.__send(uploader.NSH_REBOOT)
//            self.port.flush()
//    self.port.baudrate = self.baudrate_bootloader
//    except:
//            try:
//            self.port.flush()
//    self.port.baudrate = self.baudrate_bootloader
//    except Exception:
//    pass
//
//        return True

}
