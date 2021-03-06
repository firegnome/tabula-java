package technology.tabula.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import technology.tabula.Cell;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import technology.tabula.TextChunk;

public final class TableSerializer implements JsonSerializer<Table> {

	public static final TableSerializer INSTANCE = new TableSerializer();

	private TableSerializer() {
		// singleton
	}

	@Override
	public JsonElement serialize(Table src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject result = new JsonObject();

		result.addProperty("extraction_method", src.getExtractionMethod());
		result.addProperty("top",    src.getTop());
		result.addProperty("left",   src.getLeft());
		result.addProperty("width",  src.getWidth());
		result.addProperty("height", src.getHeight());

		JsonArray data;
		result.add("data", data = new JsonArray());

		Set<Integer> alreadySetSpanGroups = new HashSet<>();
		
		for (List<RectangularTextContainer> srcRow : src.getRows()) {
			JsonArray row = new JsonArray();
			for (RectangularTextContainer textChunk : srcRow) {
				if (!src.isFillSpanCells() && textChunk instanceof Cell) {
					Cell cell = (Cell)textChunk;
					if (cell.getSpanGroupId() == 0 || !alreadySetSpanGroups.contains(cell.getSpanGroupId())) {
						row.add(context.serialize(textChunk));
						alreadySetSpanGroups.add(cell.getSpanGroupId());
					} else {
						Cell emptyCell = new Cell(0,0,0,0);
						emptyCell.setSpanning(true);
						emptyCell.setSpanGroupId(cell.getSpanGroupId());
						row.add(context.serialize(emptyCell));
					}
				} else {
					row.add(context.serialize(textChunk));
				}
			}
			data.add(row);
		}

		return result;
	}

}
