package protocolsupportlegacysupport.hologram.armorstand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import protocolsupport.api.Connection;
import protocolsupportlegacysupport.hologram.legacyhologram.LegacyHologram;
import protocolsupportlegacysupport.utils.Constants;

public class ArmorStandData {

	private final Connection connection;

	private Vector location;
	private final HashMap<Integer, Object> meta = new HashMap<>();

	private LegacyHologram hologram;

	public ArmorStandData(Connection connection, Vector location) {
		this.connection = connection;
		this.location = location.clone();
	}

	public void setLocation(Vector vector) {
		this.location = vector.clone();
		if (hologram != null) {
			hologram.updateLocation(connection, location.clone());
		}
	}

	public void addMeta(WrappedDataWatcher watcher) {
		addMeta(watcher.getWatchableObjects());
	}

	public void addMeta(Collection<WrappedWatchableObject> objects) {
		for (WrappedWatchableObject obj : objects) {
			meta.put(obj.getIndex(), obj.getRawValue());
		}
		if (hologram == null) {
			if (isHologram()) {
				hologram = LegacyHologram.create(connection.getVersion());
				hologram.spawn(connection, location.clone(), getName());
			}
		} else {
			hologram.updateName(connection, getName());
		}
	}


	private static final boolean isOffsetSet(int value, int offset) {
		return (value & offset) == offset;
	}

	@SuppressWarnings("unchecked")
	private Optional<WrappedChatComponent> getName() {
		return (Optional<WrappedChatComponent>) meta.get(Constants.DW_NAME_INDEX);
	}

	private boolean isHologram() {
		Object basicData = meta.get(Constants.DW_BASICDATA_INDEX);
		if (basicData == null) {
			return false;
		}
		int basicDataI = ((Number) basicData).intValue();
		if (!isOffsetSet(basicDataI, Constants.DW_BASICADATA_INVISIBLE_OFFSET)) {
			return false;
		}
		Object armorStandData = meta.get(Constants.DW_ARMORSTANDDATA_INDEX);
		if (armorStandData == null) {
			return false;
		}
		return isOffsetSet(((Number) armorStandData).intValue(), Constants.DW_ARMORSTANDDATA_MARKER_OFFSET);
	}

	public void destroy() {
		if (hologram != null) {
			hologram.despawn(connection);
			hologram = null;
		}
		location = null;
		meta.clear();
	}

}