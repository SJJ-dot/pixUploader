package com.skylin.mavlink.connection.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.skylin.mavlink.connection.MavLinkConnection;
import com.skylin.mavlink.model.ConnectionParameter;
import com.skylin.mavlink.model.UsbConnectionParameter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import sjj.alog.Log;

public class UsbConnection extends MavLinkConnection {

	private static final int FTDI_DEVICE_VENDOR_ID = 0x0403;

	private Context context;
	protected final UsbConnectionParameter usbConnectionParameter ;

	private UsbConnectionImpl mUsbConnection;

	public UsbConnection(Context context, UsbConnectionParameter usbConnectionParameter) {
		this.context = context;
		this.usbConnectionParameter = usbConnectionParameter;
	}

	@Override
	protected void closeConnection() throws IOException {
		if (mUsbConnection != null) {
			mUsbConnection.closeUsbConnection();
		}
	}

	@Override
	protected void openConnection() throws IOException {
		if (mUsbConnection != null) {
			try {
				mUsbConnection.openUsbConnection();
				Log.e("Reusing previous usb connection.");
				return;
			} catch (IOException e) {
				Log.e("Previous usb connection is not usable.", e);
				mUsbConnection = null;
			}
		}

		if (isFTDIdevice(context)) {
			final UsbConnectionImpl tmp = new UsbFTDIConnection(context, usbConnectionParameter.getBaudRate());
			try {
				tmp.openUsbConnection();

				// If the call above is successful, 'mUsbConnection' will be set.
				mUsbConnection = tmp;
				Log.e("Using FTDI usb connection.");
			} catch (IOException e) {
				Log.e("Unable to open a ftdi usb connection. Falling back to the open "
						+ "usb-library.", e);
			}
		}

		// Fallback
		if (mUsbConnection == null) {
			final UsbConnectionImpl tmp = new UsbCDCConnection(context, usbConnectionParameter.getBaudRate());

			// If an error happens here, let it propagate up the call chain since this is the
			// fallback.
			tmp.openUsbConnection();
			mUsbConnection = tmp;
			Log.e("Using open-source usb connection.");
		}
	}

	private static boolean isFTDIdevice(Context context) {
		UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		final HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		if (deviceList == null || deviceList.isEmpty()) {
			return false;
		}

		for (Entry<String, UsbDevice> device : deviceList.entrySet()) {
			if (device.getValue().getVendorId() == FTDI_DEVICE_VENDOR_ID) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected int readDataBlock(byte[] buffer) throws IOException {
		if (mUsbConnection == null) {
			throw new IOException("Uninitialized usb connection.");
		}

		return mUsbConnection.readDataBlock(buffer);
	}

	@Override
	protected void sendBuffer(byte[] buffer) throws IOException {
		if (mUsbConnection == null) {
			throw new IOException("Uninitialized usb connection.");
		}

		mUsbConnection.sendBuffer(buffer);
	}

	@Override
	public int getConnectionType() {
		return ConnectionParameter.usb;
	}

	@Override
	public String toString() {
		if (mUsbConnection == null) {
			return super.toString();
		}

		return mUsbConnection.toString();
	}

	static abstract class UsbConnectionImpl {
		protected final int mBaudRate;
		protected final Context mContext;

		protected UsbConnectionImpl(Context context, int baudRate) {
			mContext = context;
			mBaudRate = baudRate;
		}

		protected abstract void closeUsbConnection() throws IOException;

		protected abstract void openUsbConnection() throws IOException;

		protected abstract int readDataBlock(byte[] readData) throws IOException;

		protected abstract void sendBuffer(byte[] buffer);
	}
}
