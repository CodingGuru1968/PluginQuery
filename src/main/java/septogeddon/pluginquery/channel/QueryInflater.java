package septogeddon.pluginquery.channel;

import septogeddon.pluginquery.QueryChannelHandler;
import septogeddon.pluginquery.api.QueryConnection;
import septogeddon.pluginquery.api.QueryContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class QueryInflater extends QueryChannelHandler {

	public QueryInflater() {
		super(QueryContext.HANDLER_INFLATER);
	}

	public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
		if (data.length <= 0)
			return data;
		Inflater inflater = new Inflater();
		try {
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				if (count == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
					throw new DataFormatException("Incomplete or invalid compressed data");
				}
				outputStream.write(buffer, 0, count);
			}
			return outputStream.toByteArray();
		} finally {
			inflater.end();
		}
	}

	@Override
	public byte[] onReceiving(QueryConnection connection, byte[] bytes) throws Exception {
		return super.onReceiving(connection, decompress(bytes));
	}
}
