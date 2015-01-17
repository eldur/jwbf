package net.sourceforge.jwbf.mediawiki.actions.util;

public class AllPagesEnums {
    public enum ProtectionType {
        EDIT("edit"), MOVE("move");

        private String name;

        private ProtectionType(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

    }

    public enum LangLinksFilter {
        ALL("all"), WITH_LANG_LINKS("withlanglinks"), WITHOUT_LANG_LINKS("withoutlanglinks");

        private String name;

        private LangLinksFilter(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum RedirectFilter {
        ALL("all"), REDIRECTS("redirects"), NON_REDIRECTS("nonredirects");

        private String name;

        private RedirectFilter(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ProtectionLevel {
        AUTO_CONFIRMED("autoconfirmed"), SYSOP("sysop");

        private String name;

        private ProtectionLevel(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public enum Direction {
        ASCENDING("ascending"), DESCENDING("descending");

        private String name;

        private Direction(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public enum CascadeFilter {
        ALL("all"), CASCADING("cascading"), NON_CASCADING("non-cascading");

        private String name;

        private CascadeFilter(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public enum ProtectionExpiry {
        ALL("all"), DEFINITE("definite"), INDEFINITE("indefinite");

        private String name;

        private ProtectionExpiry(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}
