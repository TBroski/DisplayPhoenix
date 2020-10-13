package net.displayphoenix.blockly;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.Category;
import net.displayphoenix.blockly.gen.Module;
import net.displayphoenix.blockly.gen.impl.JavaModule;
import net.displayphoenix.blockly.gen.impl.JavaScriptModule;
import net.displayphoenix.file.DetailedFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TBroski
 */
public class Blockly {

    public static final JavaScriptModule JAVASCRIPT = new JavaScriptModule();
    public static final JavaModule JAVA = new JavaModule();

    public static final Category LOGIC = new Category() {
        @Override
        public String getName() {
            return "Logic";
        }

        @Override
        public String getColor() {
            return "%{BKY_LOGIC_HUE}";
        }
    };
    public static final Category FLOW = new Category() {
        @Override
        public String getName() {
            return "Flow control";
        }

        @Override
        public String getColor() {
            return "%{BKY_PROCEDURES_HUE}";
        }
    };
    public static final Category MATH = new Category() {
        @Override
        public String getName() {
            return "Math";
        }

        @Override
        public String getColor() {
            return "%{BKY_MATH_HUE}";
        }
    };
    public static final Category TEXT = new Category() {
        @Override
        public String getName() {
            return "Text";
        }

        @Override
        public String getColor() {
            return "%{BKY_TEXTS_HUE}";
        }
    };

    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static final Map<Category, List<Block>> BLOCKS = new HashMap<>();

    public static void registerCategory(Category category) {
        if (!BLOCKS.containsKey(category)) {
            BLOCKS.put(category, new ArrayList<>());
        }
    }
    public static void registerBlock(Block block, Category category) {
        if (!BLOCKS.containsKey(category)) {
            BLOCKS.put(category, new ArrayList<>());
        } else if (BLOCKS.get(category).contains(block)) {
            System.out.println("[ERROR] Block: " + block.getType() + ". Already registered, skipping.");
            return;
        }
        BLOCKS.get(category).add(block);
        if (block.isCustom())
            System.out.println("[BLOCKLY] Registered block: " + block.getType() + ".");
    }
    public static void registerBlock(File blockJson, Category category) {
        try {
            JsonObject blockObject = gson.fromJson(new FileReader(blockJson), JsonObject.class);
            Block block = new Block(new DetailedFile(blockJson).getFileName(), blockObject.get("init") != null ? blockObject.get("init").getAsString() : null, blockObject);
            block.depend(blockObject.get("depend") != null ? blockObject.get("depend").getAsString() : null);
            block.provide(blockObject.get("provide") != null ? blockObject.get("provide").getAsString() : null);
            if (blockObject.get("fieldProvides") != null) {
                Map<String, Map<String, String[]>> fieldProvides = gson.fromJson(blockObject.get("fieldProvides").toString(), new TypeToken<Map<String, Map<String, String[]>>>() {}.getType());
                for (String fieldKey : fieldProvides.keySet()) {
                    block.fieldProvide(fieldKey, fieldProvides.get(fieldKey));
                }
            }
            if (blockObject.get("code") != null) {
                Map<String, String> code = gson.fromJson(blockObject.get("code").toString(), new TypeToken<Map<String, String>>() {}.getType());
                for (String codeKey : code.keySet()) {
                    getModuleFromName(codeKey).registerBlockCode(block, code.get(codeKey));
                }
            }
            if (blockObject.get("fieldManipulator") != null) {
                Map<String, Map<String, Map<String, String>>> fieldManipulator = gson.fromJson(blockObject.get("fieldManipulator").toString(), new TypeToken<Map<String, Map<String, Map<String, String>>>>() {}.getType());
                for (String moduleKey : fieldManipulator.keySet()) {
                    getModuleFromName(moduleKey).manipulateField(block, field -> {
                        for (String fieldKey : fieldManipulator.get(moduleKey).keySet()) {
                            if (field.getKey().equalsIgnoreCase(fieldKey)) {
                                for (String fieldValue : fieldManipulator.get(moduleKey).get(fieldKey).keySet()) {
                                    if (field.getValue().equalsIgnoreCase(fieldValue)) {
                                        return fieldManipulator.get(moduleKey).get(fieldKey).get(fieldValue);
                                    }
                                }
                                break;
                            }
                        }
                        return field.getValue();
                    });
                }
            }
            if (blockObject.get("escape") != null) {
                Map<String, Boolean> escape = gson.fromJson(blockObject.get("escape").toString(), new TypeToken<Map<String, Boolean>>() {}.getType());
                for (String module : escape.keySet()) {
                    if (escape.get(module))
                        getModuleFromName(module).escapeSyntax(block);
                }
            }
            registerBlock(block, category);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String parseBlocks() {
        JsonArray array = new JsonArray();
        for (List<Block> blocks : BLOCKS.values()) {
            for (Block block : blocks) {
                if (block.isCustom()) {
                    block.getBlocklyJson().add("type", new JsonPrimitive(block.getType()));
                    array.add(block.getBlocklyJson());
                }
            }
        }
        return array.toString();
    }

    public static void appendCategories(StringBuilder builder, Category... categories) {
        for (Category category : categories) {
            builder.append("    <category name=\"" + category.getName() + "\" colour=\"" + category.getColor() + "\"> \n");
            for (Block block : BLOCKS.get(category)) {
                if (!block.isHidden()) {
                    builder.append("        <block type=\"" + block.getType() + "\"> \n");
                    if (block.getInit() != null) {
                        builder.append("            " + block.getInit() + "\n");
                    }
                    builder.append("        </block> \n");
                }
            }
            builder.append("    </category> \n");
        }
    }

    public static Category[] getBlocklyCategories() {
        Category[] categories = new Category[BLOCKS.keySet().size()];
        categories = BLOCKS.keySet().toArray(categories);
        return categories;
    }

    public static Block[] getBlocksFromCategory(Category category) {
        Block[] blockArray = new Block[BLOCKS.get(category).size()];
        blockArray = BLOCKS.get(category).toArray(blockArray);
        return blockArray;
    }

    public static Block getBlockFromType(String type) {
        for (Category category : BLOCKS.keySet()) {
            for (Block block : BLOCKS.get(category)) {
                if (block.getType().equalsIgnoreCase(type)) {
                    return block;
                }
            }
        }
        return null;
    }

    public static Category getCategoryFromType(String type) {
        for (Category category : BLOCKS.keySet()) {
            if (category.getName().equalsIgnoreCase(type)) {
                return category;
            }
        }
        return null;
    }

    public static Module getModuleFromName(String name) {
        if (name.equalsIgnoreCase("java")) {
            return JAVA;
        }
        else if (name.equalsIgnoreCase("javascript")) {
            return JAVASCRIPT;
        }
        return null;
    }

    public static void queueFlowControl() {
        Block ifBlock = new Block("controls_if");
        Blockly.registerBlock(ifBlock, FLOW);
        JAVA.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
        JAVA.escapeSyntax(ifBlock);
        JAVA.attachMutator(ifBlock, (mutation, index) -> {
            if (mutation.equalsIgnoreCase("elseif")) {
                return "else if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
            }
            else if (mutation.equalsIgnoreCase("else")) {
                return "else {\n$[statement%ELSE]\n}";
            }
            return null;
        });
        JAVASCRIPT.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
        JAVASCRIPT.escapeSyntax(ifBlock);
        JAVASCRIPT.attachMutator(ifBlock, (mutation, index) -> {
            if (mutation.equalsIgnoreCase("elseif")) {
                return "else if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
            }
            else if (mutation.equalsIgnoreCase("else")) {
                return "else {\n$[statement%ELSE]\n}";
            }
            return null;
        });

        Block repeatBlock = new Block("controls_repeat_ext");
        Blockly.registerBlock(repeatBlock, FLOW);
        JAVA.registerBlockCode(repeatBlock, "for (int $[increment%repeat] = 0; $[increment%repeat] < $[value%TIMES]; $[increment%repeat]++) {\n$[statement%DO]\n}");
        JAVA.escapeSyntax(repeatBlock);
        JAVASCRIPT.registerBlockCode(repeatBlock, "var $[increment%repeat]; for ($[increment%repeat] = 0; $[increment%repeat] < $[value%TIMES]; $[increment%repeat]++) {\n$[statement%DO]\n}");
        JAVASCRIPT.escapeSyntax(repeatBlock);
    }
    public static void queueLogic() {
        Block compareBlock = new Block("logic_compare");
        Blockly.registerBlock(compareBlock, LOGIC);
        JAVA.registerBlockCode(compareBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVA.manipulateField(compareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "==";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!=";
                if (field.getValue().equalsIgnoreCase("LT")) return "<";
                if (field.getValue().equalsIgnoreCase("LTE")) return "<=";
                if (field.getValue().equalsIgnoreCase("GT")) return ">";
                if (field.getValue().equalsIgnoreCase("GTE")) return ">=";
            }
            return field.getValue();
        });
        JAVA.escapeSyntax(compareBlock);
        JAVASCRIPT.registerBlockCode(compareBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVASCRIPT.manipulateField(compareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "===";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!==";
                if (field.getValue().equalsIgnoreCase("LT")) return "<";
                if (field.getValue().equalsIgnoreCase("LTE")) return "<=";
                if (field.getValue().equalsIgnoreCase("GT")) return ">";
                if (field.getValue().equalsIgnoreCase("GTE")) return ">=";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(compareBlock);

        Block negateBlock = new Block("logic_negate");
        Blockly.registerBlock(negateBlock, LOGIC);
        JAVA.registerBlockCode(negateBlock, "!($[value%BOOL])");
        JAVA.escapeSyntax(negateBlock);
        JAVASCRIPT.registerBlockCode(negateBlock, "!($[value%BOOL])");
        JAVASCRIPT.escapeSyntax(negateBlock);

        Block booleanBlock = new Block("logic_boolean");
        Blockly.registerBlock(booleanBlock, LOGIC);
        JAVA.registerBlockCode(booleanBlock, "$[field%BOOL]");
        JAVA.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
        JAVA.escapeSyntax(booleanBlock);
        JAVASCRIPT.registerBlockCode(booleanBlock, "$[field%BOOL]");
        JAVASCRIPT.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
        JAVASCRIPT.escapeSyntax(booleanBlock);

        Blockly.registerBlock(new File("src/main/resources/blockly/text_compare.json"), Blockly.LOGIC);
        Block textCompareBlock = Blockly.getBlockFromType("text_compare");
        JAVASCRIPT.manipulateField(textCompareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "===";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!==";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(textCompareBlock);
        JAVA.manipulateField(textCompareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "==";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!=";
            }
            return field.getValue();
        });
        JAVA.escapeSyntax(textCompareBlock);
    }
    public static void queueMath() {
        Block numberBlock = new Block("math_number");
        Blockly.registerBlock(numberBlock, MATH);
        JAVA.registerBlockCode(numberBlock, "$[field%NUM]");
        JAVA.escapeSyntax(numberBlock);
        JAVASCRIPT.registerBlockCode(numberBlock, "$[field%NUM]");
        JAVASCRIPT.escapeSyntax(numberBlock);

        Blockly.registerBlock(new File("src/main/resources/blockly/math_arithmetic_custom.json"), MATH);
        Block arithmeticBlock = Blockly.getBlockFromType("math_arithmetic_custom");
        JAVA.registerBlockCode(arithmeticBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVA.manipulateField(arithmeticBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("ADD")) return "+";
                if (field.getValue().equalsIgnoreCase("MINUS")) return "-";
                if (field.getValue().equalsIgnoreCase("MULTIPLY")) return "*";
                if (field.getValue().equalsIgnoreCase("DIVIDE")) return "/";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(arithmeticBlock);
        JAVASCRIPT.registerBlockCode(arithmeticBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVASCRIPT.manipulateField(arithmeticBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("ADD")) return "+";
                if (field.getValue().equalsIgnoreCase("MINUS")) return "-";
                if (field.getValue().equalsIgnoreCase("MULTIPLY")) return "*";
                if (field.getValue().equalsIgnoreCase("DIVIDE")) return "/";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(arithmeticBlock);
    }
    public static void queueText() {
        Block textBlock = new Block("text");
        Blockly.registerBlock(textBlock, TEXT);
        JAVA.registerBlockCode(textBlock, "\"$[field%TEXT]\"");
        JAVA.escapeSyntax(textBlock);
        JAVASCRIPT.registerBlockCode(textBlock, "'$[field%TEXT]'");
        JAVASCRIPT.escapeSyntax(textBlock);

        Block printBlock = new Block("text_print");
        Blockly.registerBlock(printBlock, TEXT);
        JAVA.registerBlockCode(printBlock, "System.out.println($[value%TEXT])");
        JAVASCRIPT.registerBlockCode(printBlock, "console.log($[value%TEXT])");

        Blockly.registerBlock(new File("src/main/resources/blockly/string_length.json"), TEXT);
        Blockly.registerBlock(new File("src/main/resources/blockly/text_arithmetic.json"), TEXT);
        Blockly.registerBlock(new File("src/main/resources/blockly/text_substring.json"), TEXT);
    }
}
