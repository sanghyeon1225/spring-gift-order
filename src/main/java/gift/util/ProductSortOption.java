package gift.util;

public enum ProductSortOption {
    ID("id"),
    NAME("name"),
    PRICE("price");

    private final String value;

    ProductSortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String value) {
        for (ProductSortOption option : ProductSortOption.values()) {
            if (option.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

}
