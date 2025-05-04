package std.ietf;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防止重放攻击的一次性密码
 *
 * @author iMinusMinus
 * @date 2025-05-04
 */
public interface OneTimePassword<C, S> {

    /**
     * 生成一次性口令
     *
     * @param client 客户端密码/口令等信息
     * @param server 服务端随机种子等信息
     * @return 一次性口令
     */
    String generate(C client, S server);

    /**
     * <a href="https://datatracker.ietf.org/doc/html/rfc2289">A One-Time Password System</a>
     * 规范定义系统登录认证为抵御重放攻击，使用摘要算法（md4、md5、sha1）计算出一致密码。
     * 该规范参考了<a href="https://www.rfc-editor.org/rfc/rfc1760">The S/KEY One-Time Password System</a>.
     * <p>
     * 服务端首先生成一个挑战码(otp-alg sequence seed格式)，OTP生成器根据口令和挑战码经过多轮迭代计算出（或查找到）OTP。
     * 服务端数据库包含用户首个或上次验证成功的OTP，验证时，服务端将收到的OTP进行解码，然后进行计算。
     * 如果计算结果和保存的结果一致，则认证通过，并将此次的OTP保存备用。
     */
    class OTP implements OneTimePassword<String, byte[]> {

        /**
         * @see sun.security.provider.MD4
         */
        private static final Provider MD4_PROVIDER = new Provider("MD4Provider", 1.8D, "MD4 MessageDigest") {
            private static final long serialVersionUID = -8850464997518327965L;
        };

        static {
            MD4_PROVIDER.put("MessageDigest.MD4", "sun.security.provider.MD4");
        }

        private static final Pattern CHALLENGE_SYNTAX = Pattern.compile("^otp-(?<alg>[a-z0-9]+)[\\x20\t]+(?<count>[0-9]+)[\\x20\t]+(?<seed>[a-zA-Z0-9]+)[\\x20\n]+$");

        private static final String[] STANDARD_DICTIONARY = {"A", "ABE", "ACE", "ACT", "AD", "ADA", "ADD",
                "AGO", "AID", "AIM", "AIR", "ALL", "ALP", "AM", "AMY",
                "AN", "ANA", "AND", "ANN", "ANT", "ANY", "APE", "APS",
                "APT", "ARC", "ARE", "ARK", "ARM", "ART", "AS", "ASH",
                "ASK", "AT", "ATE", "AUG", "AUK", "AVE", "AWE", "AWK",
                "AWL", "AWN", "AX", "AYE", "BAD", "BAG", "BAH", "BAM",
                "BAN", "BAR", "BAT", "BAY", "BE", "BED", "BEE", "BEG",
                "BEN", "BET", "BEY", "BIB", "BID", "BIG", "BIN", "BIT",
                "BOB", "BOG", "BON", "BOO", "BOP", "BOW", "BOY", "BUB",
                "BUD", "BUG", "BUM", "BUN", "BUS", "BUT", "BUY", "BY",
                "BYE", "CAB", "CAL", "CAM", "CAN", "CAP", "CAR", "CAT",
                "CAW", "COD", "COG", "COL", "CON", "COO", "COP", "COT",
                "COW", "COY", "CRY", "CUB", "CUE", "CUP", "CUR", "CUT",
                "DAB", "DAD", "DAM", "DAN", "DAR", "DAY", "DEE", "DEL",
                "DEN", "DES", "DEW", "DID", "DIE", "DIG", "DIN", "DIP",
                "DO", "DOE", "DOG", "DON", "DOT", "DOW", "DRY", "DUB",
                "DUD", "DUE", "DUG", "DUN", "EAR", "EAT", "ED", "EEL",
                "EGG", "EGO", "ELI", "ELK", "ELM", "ELY", "EM", "END",
                "EST", "ETC", "EVA", "EVE", "EWE", "EYE", "FAD", "FAN",
                "FAR", "FAT", "FAY", "FED", "FEE", "FEW", "FIB", "FIG",
                "FIN", "FIR", "FIT", "FLO", "FLY", "FOE", "FOG", "FOR",
                "FRY", "FUM", "FUN", "FUR", "GAB", "GAD", "GAG", "GAL",
                "GAM", "GAP", "GAS", "GAY", "GEE", "GEL", "GEM", "GET",
                "GIG", "GIL", "GIN", "GO", "GOT", "GUM", "GUN", "GUS",
                "GUT", "GUY", "GYM", "GYP", "HA", "HAD", "HAL", "HAM",
                "HAN", "HAP", "HAS", "HAT", "HAW", "HAY", "HE", "HEM",
                "HEN", "HER", "HEW", "HEY", "HI", "HID", "HIM", "HIP",
                "HIS", "HIT", "HO", "HOB", "HOC", "HOE", "HOG", "HOP",
                "HOT", "HOW", "HUB", "HUE", "HUG", "HUH", "HUM", "HUT",
                "I", "ICY", "IDA", "IF", "IKE", "ILL", "INK", "INN",
                "IO", "ION", "IQ", "IRA", "IRE", "IRK", "IS", "IT",
                "ITS", "IVY", "JAB", "JAG", "JAM", "JAN", "JAR", "JAW",
                "JAY", "JET", "JIG", "JIM", "JO", "JOB", "JOE", "JOG",
                "JOT", "JOY", "JUG", "JUT", "KAY", "KEG", "KEN", "KEY",
                "KID", "KIM", "KIN", "KIT", "LA", "LAB", "LAC", "LAD",
                "LAG", "LAM", "LAP", "LAW", "LAY", "LEA", "LED", "LEE",
                "LEG", "LEN", "LEO", "LET", "LEW", "LID", "LIE", "LIN",
                "LIP", "LIT", "LO", "LOB", "LOG", "LOP", "LOS", "LOT",
                "LOU", "LOW", "LOY", "LUG", "LYE", "MA", "MAC", "MAD",
                "MAE", "MAN", "MAO", "MAP", "MAT", "MAW", "MAY", "ME",
                "MEG", "MEL", "MEN", "MET", "MEW", "MID", "MIN", "MIT",
                "MOB", "MOD", "MOE", "MOO", "MOP", "MOS", "MOT", "MOW",
                "MUD", "MUG", "MUM", "MY", "NAB", "NAG", "NAN", "NAP",
                "NAT", "NAY", "NE", "NED", "NEE", "NET", "NEW", "NIB",
                "NIL", "NIP", "NIT", "NO", "NOB", "NOD", "NON", "NOR",
                "NOT", "NOV", "NOW", "NU", "NUN", "NUT", "O", "OAF",
                "OAK", "OAR", "OAT", "ODD", "ODE", "OF", "OFF", "OFT",
                "OH", "OIL", "OK", "OLD", "ON", "ONE", "OR", "ORB",
                "ORE", "ORR", "OS", "OTT", "OUR", "OUT", "OVA", "OW",
                "OWE", "OWL", "OWN", "OX", "PA", "PAD", "PAL", "PAM",
                "PAN", "PAP", "PAR", "PAT", "PAW", "PAY", "PEA", "PEG",
                "PEN", "PEP", "PER", "PET", "PEW", "PHI", "PI", "PIE",
                "PIN", "PIT", "PLY", "PO", "POD", "POE", "POP", "POT",
                "POW", "PRO", "PRY", "PUB", "PUG", "PUN", "PUP", "PUT",
                "QUO", "RAG", "RAM", "RAN", "RAP", "RAT", "RAW", "RAY",
                "REB", "RED", "REP", "RET", "RIB", "RID", "RIG", "RIM",
                "RIO", "RIP", "ROB", "ROD", "ROE", "RON", "ROT", "ROW",
                "ROY", "RUB", "RUE", "RUG", "RUM", "RUN", "RYE", "SAC",
                "SAD", "SAG", "SAL", "SAM", "SAN", "SAP", "SAT", "SAW",
                "SAY", "SEA", "SEC", "SEE", "SEN", "SET", "SEW", "SHE",
                "SHY", "SIN", "SIP", "SIR", "SIS", "SIT", "SKI", "SKY",
                "SLY", "SO", "SOB", "SOD", "SON", "SOP", "SOW", "SOY",
                "SPA", "SPY", "SUB", "SUD", "SUE", "SUM", "SUN", "SUP",
                "TAB", "TAD", "TAG", "TAN", "TAP", "TAR", "TEA", "TED",
                "TEE", "TEN", "THE", "THY", "TIC", "TIE", "TIM", "TIN",
                "TIP", "TO", "TOE", "TOG", "TOM", "TON", "TOO", "TOP",
                "TOW", "TOY", "TRY", "TUB", "TUG", "TUM", "TUN", "TWO",
                "UN", "UP", "US", "USE", "VAN", "VAT", "VET", "VIE",
                "WAD", "WAG", "WAR", "WAS", "WAY", "WE", "WEB", "WED",
                "WEE", "WET", "WHO", "WHY", "WIN", "WIT", "WOK", "WON",
                "WOO", "WOW", "WRY", "WU", "YAM", "YAP", "YAW", "YE",
                "YEA", "YES", "YET", "YOU", "ABED", "ABEL", "ABET", "ABLE",
                "ABUT", "ACHE", "ACID", "ACME", "ACRE", "ACTA", "ACTS", "ADAM",
                "ADDS", "ADEN", "AFAR", "AFRO", "AGEE", "AHEM", "AHOY", "AIDA",
                "AIDE", "AIDS", "AIRY", "AJAR", "AKIN", "ALAN", "ALEC", "ALGA",
                "ALIA", "ALLY", "ALMA", "ALOE", "ALSO", "ALTO", "ALUM", "ALVA",
                "AMEN", "AMES", "AMID", "AMMO", "AMOK", "AMOS", "AMRA", "ANDY",
                "ANEW", "ANNA", "ANNE", "ANTE", "ANTI", "AQUA", "ARAB", "ARCH",
                "AREA", "ARGO", "ARID", "ARMY", "ARTS", "ARTY", "ASIA", "ASKS",
                "ATOM", "AUNT", "AURA", "AUTO", "AVER", "AVID", "AVIS", "AVON",
                "AVOW", "AWAY", "AWRY", "BABE", "BABY", "BACH", "BACK", "BADE",
                "BAIL", "BAIT", "BAKE", "BALD", "BALE", "BALI", "BALK", "BALL",
                "BALM", "BAND", "BANE", "BANG", "BANK", "BARB", "BARD", "BARE",
                "BARK", "BARN", "BARR", "BASE", "BASH", "BASK", "BASS", "BATE",
                "BATH", "BAWD", "BAWL", "BEAD", "BEAK", "BEAM", "BEAN", "BEAR",
                "BEAT", "BEAU", "BECK", "BEEF", "BEEN", "BEER", "BEET", "BELA",
                "BELL", "BELT", "BEND", "BENT", "BERG", "BERN", "BERT", "BESS",
                "BEST", "BETA", "BETH", "BHOY", "BIAS", "BIDE", "BIEN", "BILE",
                "BILK", "BILL", "BIND", "BING", "BIRD", "BITE", "BITS", "BLAB",
                "BLAT", "BLED", "BLEW", "BLOB", "BLOC", "BLOT", "BLOW", "BLUE",
                "BLUM", "BLUR", "BOAR", "BOAT", "BOCA", "BOCK", "BODE", "BODY",
                "BOGY", "BOHR", "BOIL", "BOLD", "BOLO", "BOLT", "BOMB", "BONA",
                "BOND", "BONE", "BONG", "BONN", "BONY", "BOOK", "BOOM", "BOON",
                "BOOT", "BORE", "BORG", "BORN", "BOSE", "BOSS", "BOTH", "BOUT",
                "BOWL", "BOYD", "BRAD", "BRAE", "BRAG", "BRAN", "BRAY", "BRED",
                "BREW", "BRIG", "BRIM", "BROW", "BUCK", "BUDD", "BUFF", "BULB",
                "BULK", "BULL", "BUNK", "BUNT", "BUOY", "BURG", "BURL", "BURN",
                "BURR", "BURT", "BURY", "BUSH", "BUSS", "BUST", "BUSY", "BYTE",
                "CADY", "CAFE", "CAGE", "CAIN", "CAKE", "CALF", "CALL", "CALM",
                "CAME", "CANE", "CANT", "CARD", "CARE", "CARL", "CARR", "CART",
                "CASE", "CASH", "CASK", "CAST", "CAVE", "CEIL", "CELL", "CENT",
                "CERN", "CHAD", "CHAR", "CHAT", "CHAW", "CHEF", "CHEN", "CHEW",
                "CHIC", "CHIN", "CHOU", "CHOW", "CHUB", "CHUG", "CHUM", "CITE",
                "CITY", "CLAD", "CLAM", "CLAN", "CLAW", "CLAY", "CLOD", "CLOG",
                "CLOT", "CLUB", "CLUE", "COAL", "COAT", "COCA", "COCK", "COCO",
                "CODA", "CODE", "CODY", "COED", "COIL", "COIN", "COKE", "COLA",
                "COLD", "COLT", "COMA", "COMB", "COME", "COOK", "COOL", "COON",
                "COOT", "CORD", "CORE", "CORK", "CORN", "COST", "COVE", "COWL",
                "CRAB", "CRAG", "CRAM", "CRAY", "CREW", "CRIB", "CROW", "CRUD",
                "CUBA", "CUBE", "CUFF", "CULL", "CULT", "CUNY", "CURB", "CURD",
                "CURE", "CURL", "CURT", "CUTS", "DADE", "DALE", "DAME", "DANA",
                "DANE", "DANG", "DANK", "DARE", "DARK", "DARN", "DART", "DASH",
                "DATA", "DATE", "DAVE", "DAVY", "DAWN", "DAYS", "DEAD", "DEAF",
                "DEAL", "DEAN", "DEAR", "DEBT", "DECK", "DEED", "DEEM", "DEER",
                "DEFT", "DEFY", "DELL", "DENT", "DENY", "DESK", "DIAL", "DICE",
                "DIED", "DIET", "DIME", "DINE", "DING", "DINT", "DIRE", "DIRT",
                "DISC", "DISH", "DISK", "DIVE", "DOCK", "DOES", "DOLE", "DOLL",
                "DOLT", "DOME", "DONE", "DOOM", "DOOR", "DORA", "DOSE", "DOTE",
                "DOUG", "DOUR", "DOVE", "DOWN", "DRAB", "DRAG", "DRAM", "DRAW",
                "DREW", "DRUB", "DRUG", "DRUM", "DUAL", "DUCK", "DUCT", "DUEL",
                "DUET", "DUKE", "DULL", "DUMB", "DUNE", "DUNK", "DUSK", "DUST",
                "DUTY", "EACH", "EARL", "EARN", "EASE", "EAST", "EASY", "EBEN",
                "ECHO", "EDDY", "EDEN", "EDGE", "EDGY", "EDIT", "EDNA", "EGAN",
                "ELAN", "ELBA", "ELLA", "ELSE", "EMIL", "EMIT", "EMMA", "ENDS",
                "ERIC", "EROS", "EVEN", "EVER", "EVIL", "EYED", "FACE", "FACT",
                "FADE", "FAIL", "FAIN", "FAIR", "FAKE", "FALL", "FAME", "FANG",
                "FARM", "FAST", "FATE", "FAWN", "FEAR", "FEAT", "FEED", "FEEL",
                "FEET", "FELL", "FELT", "FEND", "FERN", "FEST", "FEUD", "FIEF",
                "FIGS", "FILE", "FILL", "FILM", "FIND", "FINE", "FINK", "FIRE",
                "FIRM", "FISH", "FISK", "FIST", "FITS", "FIVE", "FLAG", "FLAK",
                "FLAM", "FLAT", "FLAW", "FLEA", "FLED", "FLEW", "FLIT", "FLOC",
                "FLOG", "FLOW", "FLUB", "FLUE", "FOAL", "FOAM", "FOGY", "FOIL",
                "FOLD", "FOLK", "FOND", "FONT", "FOOD", "FOOL", "FOOT", "FORD",
                "FORE", "FORK", "FORM", "FORT", "FOSS", "FOUL", "FOUR", "FOWL",
                "FRAU", "FRAY", "FRED", "FREE", "FRET", "FREY", "FROG", "FROM",
                "FUEL", "FULL", "FUME", "FUND", "FUNK", "FURY", "FUSE", "FUSS",
                "GAFF", "GAGE", "GAIL", "GAIN", "GAIT", "GALA", "GALE", "GALL",
                "GALT", "GAME", "GANG", "GARB", "GARY", "GASH", "GATE", "GAUL",
                "GAUR", "GAVE", "GAWK", "GEAR", "GELD", "GENE", "GENT", "GERM",
                "GETS", "GIBE", "GIFT", "GILD", "GILL", "GILT", "GINA", "GIRD",
                "GIRL", "GIST", "GIVE", "GLAD", "GLEE", "GLEN", "GLIB", "GLOB",
                "GLOM", "GLOW", "GLUE", "GLUM", "GLUT", "GOAD", "GOAL", "GOAT",
                "GOER", "GOES", "GOLD", "GOLF", "GONE", "GONG", "GOOD", "GOOF",
                "GORE", "GORY", "GOSH", "GOUT", "GOWN", "GRAB", "GRAD", "GRAY",
                "GREG", "GREW", "GREY", "GRID", "GRIM", "GRIN", "GRIT", "GROW",
                "GRUB", "GULF", "GULL", "GUNK", "GURU", "GUSH", "GUST", "GWEN",
                "GWYN", "HAAG", "HAAS", "HACK", "HAIL", "HAIR", "HALE", "HALF",
                "HALL", "HALO", "HALT", "HAND", "HANG", "HANK", "HANS", "HARD",
                "HARK", "HARM", "HART", "HASH", "HAST", "HATE", "HATH", "HAUL",
                "HAVE", "HAWK", "HAYS", "HEAD", "HEAL", "HEAR", "HEAT", "HEBE",
                "HECK", "HEED", "HEEL", "HEFT", "HELD", "HELL", "HELM", "HERB",
                "HERD", "HERE", "HERO", "HERS", "HESS", "HEWN", "HICK", "HIDE",
                "HIGH", "HIKE", "HILL", "HILT", "HIND", "HINT", "HIRE", "HISS",
                "HIVE", "HOBO", "HOCK", "HOFF", "HOLD", "HOLE", "HOLM", "HOLT",
                "HOME", "HONE", "HONK", "HOOD", "HOOF", "HOOK", "HOOT", "HORN",
                "HOSE", "HOST", "HOUR", "HOVE", "HOWE", "HOWL", "HOYT", "HUCK",
                "HUED", "HUFF", "HUGE", "HUGH", "HUGO", "HULK", "HULL", "HUNK",
                "HUNT", "HURD", "HURL", "HURT", "HUSH", "HYDE", "HYMN", "IBIS",
                "ICON", "IDEA", "IDLE", "IFFY", "INCA", "INCH", "INTO", "IONS",
                "IOTA", "IOWA", "IRIS", "IRMA", "IRON", "ISLE", "ITCH", "ITEM",
                "IVAN", "JACK", "JADE", "JAIL", "JAKE", "JANE", "JAVA", "JEAN",
                "JEFF", "JERK", "JESS", "JEST", "JIBE", "JILL", "JILT", "JIVE",
                "JOAN", "JOBS", "JOCK", "JOEL", "JOEY", "JOHN", "JOIN", "JOKE",
                "JOLT", "JOVE", "JUDD", "JUDE", "JUDO", "JUDY", "JUJU", "JUKE",
                "JULY", "JUNE", "JUNK", "JUNO", "JURY", "JUST", "JUTE", "KAHN",
                "KALE", "KANE", "KANT", "KARL", "KATE", "KEEL", "KEEN", "KENO",
                "KENT", "KERN", "KERR", "KEYS", "KICK", "KILL", "KIND", "KING",
                "KIRK", "KISS", "KITE", "KLAN", "KNEE", "KNEW", "KNIT", "KNOB",
                "KNOT", "KNOW", "KOCH", "KONG", "KUDO", "KURD", "KURT", "KYLE",
                "LACE", "LACK", "LACY", "LADY", "LAID", "LAIN", "LAIR", "LAKE",
                "LAMB", "LAME", "LAND", "LANE", "LANG", "LARD", "LARK", "LASS",
                "LAST", "LATE", "LAUD", "LAVA", "LAWN", "LAWS", "LAYS", "LEAD",
                "LEAF", "LEAK", "LEAN", "LEAR", "LEEK", "LEER", "LEFT", "LEND",
                "LENS", "LENT", "LEON", "LESK", "LESS", "LEST", "LETS", "LIAR",
                "LICE", "LICK", "LIED", "LIEN", "LIES", "LIEU", "LIFE", "LIFT",
                "LIKE", "LILA", "LILT", "LILY", "LIMA", "LIMB", "LIME", "LIND",
                "LINE", "LINK", "LINT", "LION", "LISA", "LIST", "LIVE", "LOAD",
                "LOAF", "LOAM", "LOAN", "LOCK", "LOFT", "LOGE", "LOIS", "LOLA",
                "LONE", "LONG", "LOOK", "LOON", "LOOT", "LORD", "LORE", "LOSE",
                "LOSS", "LOST", "LOUD", "LOVE", "LOWE", "LUCK", "LUCY", "LUGE",
                "LUKE", "LULU", "LUND", "LUNG", "LURA", "LURE", "LURK", "LUSH",
                "LUST", "LYLE", "LYNN", "LYON", "LYRA", "MACE", "MADE", "MAGI",
                "MAID", "MAIL", "MAIN", "MAKE", "MALE", "MALI", "MALL", "MALT",
                "MANA", "MANN", "MANY", "MARC", "MARE", "MARK", "MARS", "MART",
                "MARY", "MASH", "MASK", "MASS", "MAST", "MATE", "MATH", "MAUL",
                "MAYO", "MEAD", "MEAL", "MEAN", "MEAT", "MEEK", "MEET", "MELD",
                "MELT", "MEMO", "MEND", "MENU", "MERT", "MESH", "MESS", "MICE",
                "MIKE", "MILD", "MILE", "MILK", "MILL", "MILT", "MIMI", "MIND",
                "MINE", "MINI", "MINK", "MINT", "MIRE", "MISS", "MIST", "MITE",
                "MITT", "MOAN", "MOAT", "MOCK", "MODE", "MOLD", "MOLE", "MOLL",
                "MOLT", "MONA", "MONK", "MONT", "MOOD", "MOON", "MOOR", "MOOT",
                "MORE", "MORN", "MORT", "MOSS", "MOST", "MOTH", "MOVE", "MUCH",
                "MUCK", "MUDD", "MUFF", "MULE", "MULL", "MURK", "MUSH", "MUST",
                "MUTE", "MUTT", "MYRA", "MYTH", "NAGY", "NAIL", "NAIR", "NAME",
                "NARY", "NASH", "NAVE", "NAVY", "NEAL", "NEAR", "NEAT", "NECK",
                "NEED", "NEIL", "NELL", "NEON", "NERO", "NESS", "NEST", "NEWS",
                "NEWT", "NIBS", "NICE", "NICK", "NILE", "NINA", "NINE", "NOAH",
                "NODE", "NOEL", "NOLL", "NONE", "NOOK", "NOON", "NORM", "NOSE",
                "NOTE", "NOUN", "NOVA", "NUDE", "NULL", "NUMB", "OATH", "OBEY",
                "OBOE", "ODIN", "OHIO", "OILY", "OINT", "OKAY", "OLAF", "OLDY",
                "OLGA", "OLIN", "OMAN", "OMEN", "OMIT", "ONCE", "ONES", "ONLY",
                "ONTO", "ONUS", "ORAL", "ORGY", "OSLO", "OTIS", "OTTO", "OUCH",
                "OUST", "OUTS", "OVAL", "OVEN", "OVER", "OWLY", "OWNS", "QUAD",
                "QUIT", "QUOD", "RACE", "RACK", "RACY", "RAFT", "RAGE", "RAID",
                "RAIL", "RAIN", "RAKE", "RANK", "RANT", "RARE", "RASH", "RATE",
                "RAVE", "RAYS", "READ", "REAL", "REAM", "REAR", "RECK", "REED",
                "REEF", "REEK", "REEL", "REID", "REIN", "RENA", "REND", "RENT",
                "REST", "RICE", "RICH", "RICK", "RIDE", "RIFT", "RILL", "RIME",
                "RING", "RINK", "RISE", "RISK", "RITE", "ROAD", "ROAM", "ROAR",
                "ROBE", "ROCK", "RODE", "ROIL", "ROLL", "ROME", "ROOD", "ROOF",
                "ROOK", "ROOM", "ROOT", "ROSA", "ROSE", "ROSS", "ROSY", "ROTH",
                "ROUT", "ROVE", "ROWE", "ROWS", "RUBE", "RUBY", "RUDE", "RUDY",
                "RUIN", "RULE", "RUNG", "RUNS", "RUNT", "RUSE", "RUSH", "RUSK",
                "RUSS", "RUST", "RUTH", "SACK", "SAFE", "SAGE", "SAID", "SAIL",
                "SALE", "SALK", "SALT", "SAME", "SAND", "SANE", "SANG", "SANK",
                "SARA", "SAUL", "SAVE", "SAYS", "SCAN", "SCAR", "SCAT", "SCOT",
                "SEAL", "SEAM", "SEAR", "SEAT", "SEED", "SEEK", "SEEM", "SEEN",
                "SEES", "SELF", "SELL", "SEND", "SENT", "SETS", "SEWN", "SHAG",
                "SHAM", "SHAW", "SHAY", "SHED", "SHIM", "SHIN", "SHOD", "SHOE",
                "SHOT", "SHOW", "SHUN", "SHUT", "SICK", "SIDE", "SIFT", "SIGH",
                "SIGN", "SILK", "SILL", "SILO", "SILT", "SINE", "SING", "SINK",
                "SIRE", "SITE", "SITS", "SITU", "SKAT", "SKEW", "SKID", "SKIM",
                "SKIN", "SKIT", "SLAB", "SLAM", "SLAT", "SLAY", "SLED", "SLEW",
                "SLID", "SLIM", "SLIT", "SLOB", "SLOG", "SLOT", "SLOW", "SLUG",
                "SLUM", "SLUR", "SMOG", "SMUG", "SNAG", "SNOB", "SNOW", "SNUB",
                "SNUG", "SOAK", "SOAR", "SOCK", "SODA", "SOFA", "SOFT", "SOIL",
                "SOLD", "SOME", "SONG", "SOON", "SOOT", "SORE", "SORT", "SOUL",
                "SOUR", "SOWN", "STAB", "STAG", "STAN", "STAR", "STAY", "STEM",
                "STEW", "STIR", "STOW", "STUB", "STUN", "SUCH", "SUDS", "SUIT",
                "SULK", "SUMS", "SUNG", "SUNK", "SURE", "SURF", "SWAB", "SWAG",
                "SWAM", "SWAN", "SWAT", "SWAY", "SWIM", "SWUM", "TACK", "TACT",
                "TAIL", "TAKE", "TALE", "TALK", "TALL", "TANK", "TASK", "TATE",
                "TAUT", "TEAL", "TEAM", "TEAR", "TECH", "TEEM", "TEEN", "TEET",
                "TELL", "TEND", "TENT", "TERM", "TERN", "TESS", "TEST", "THAN",
                "THAT", "THEE", "THEM", "THEN", "THEY", "THIN", "THIS", "THUD",
                "THUG", "TICK", "TIDE", "TIDY", "TIED", "TIER", "TILE", "TILL",
                "TILT", "TIME", "TINA", "TINE", "TINT", "TINY", "TIRE", "TOAD",
                "TOGO", "TOIL", "TOLD", "TOLL", "TONE", "TONG", "TONY", "TOOK",
                "TOOL", "TOOT", "TORE", "TORN", "TOTE", "TOUR", "TOUT", "TOWN",
                "TRAG", "TRAM", "TRAY", "TREE", "TREK", "TRIG", "TRIM", "TRIO",
                "TROD", "TROT", "TROY", "TRUE", "TUBA", "TUBE", "TUCK", "TUFT",
                "TUNA", "TUNE", "TUNG", "TURF", "TURN", "TUSK", "TWIG", "TWIN",
                "TWIT", "ULAN", "UNIT", "URGE", "USED", "USER", "USES", "UTAH",
                "VAIL", "VAIN", "VALE", "VARY", "VASE", "VAST", "VEAL", "VEDA",
                "VEIL", "VEIN", "VEND", "VENT", "VERB", "VERY", "VETO", "VICE",
                "VIEW", "VINE", "VISE", "VOID", "VOLT", "VOTE", "WACK", "WADE",
                "WAGE", "WAIL", "WAIT", "WAKE", "WALE", "WALK", "WALL", "WALT",
                "WAND", "WANE", "WANG", "WANT", "WARD", "WARM", "WARN", "WART",
                "WASH", "WAST", "WATS", "WATT", "WAVE", "WAVY", "WAYS", "WEAK",
                "WEAL", "WEAN", "WEAR", "WEED", "WEEK", "WEIR", "WELD", "WELL",
                "WELT", "WENT", "WERE", "WERT", "WEST", "WHAM", "WHAT", "WHEE",
                "WHEN", "WHET", "WHOA", "WHOM", "WICK", "WIFE", "WILD", "WILL",
                "WIND", "WINE", "WING", "WINK", "WINO", "WIRE", "WISE", "WISH",
                "WITH", "WOLF", "WONT", "WOOD", "WOOL", "WORD", "WORE", "WORK",
                "WORM", "WORN", "WOVE", "WRIT", "WYNN", "YALE", "YANG", "YANK",
                "YARD", "YARN", "YAWL", "YAWN", "YEAH", "YEAR", "YELL", "YOGA",
                "YOKE"
        };

        private final boolean sixWordFormat;

        private final String separator;

        public OTP(boolean sixWordFormat) {
            this(sixWordFormat, " ");
        }

        public OTP(boolean sixWordFormat, String separator) {
            this.sixWordFormat = sixWordFormat;
            this.separator = separator;
        }

        /**
         * @param challenge  挑战信息
         * @param passPhrase 用户密码，为避免穷举攻击/字典攻击，应不少于10个字符，建议为可视字符
         * @return OTP
         */
        @Override
        public String generate(String challenge, byte[] passPhrase) {
            Matcher matcher = CHALLENGE_SYNTAX.matcher(challenge);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("bad challenge: " + challenge);
            }
            String algorithm = matcher.group("alg"); // 摘要算法：md4, md5 或 sha1
            int times = Integer.parseInt(matcher.group("count"));
            byte[] seed = matcher.group("seed").getBytes(StandardCharsets.UTF_8); // 服务端生成的随机数种子，1-16个数字、字母组成的字符
            return generate(algorithm, times, seed, passPhrase);
        }

        public String generate(String algorithm, int times, byte[] seed, byte[] passPhrase) {
            if (passPhrase.length < 10 || passPhrase.length > 63) {
                throw new IllegalArgumentException("pass phrase should longer than 10 characters and shorter than 63 characters");
            }
            if (seed.length < 1 || seed.length > 16) { // 大小写不敏感，但需转成小写
                throw new IllegalArgumentException("Seed must be between 1 and 16 characters in length");
            }
            for (int i = 0; i < seed.length; i++) {
                if (seed[i] >= 'A' && seed[i] <= 'Z') {
                    seed[i] = (byte) (seed[i] + ('a' - 'A'));
                }
                if (seed[i] < '0' || (seed[i] > '9' && seed[i] < 'a') || seed[i] > 'z') {
                    throw new IllegalArgumentException("The seed MUST consist of purely alphanumeric characters");
                }
            }
            byte[] truncated = digest(passPhrase, algorithm, seed);
            while (times-- > 0) {
                truncated = digest(truncated, algorithm, new byte[0]);
            }
            return sixWordFormat ? bytesToSixWords(truncated, separator) : bytesToHexStr(truncated, separator);
        }

        public static byte[] digest(byte[] passPhrase, String algorithm, byte[] seed) {
            MessageDigest md;
            try {
                md = "md4".equalsIgnoreCase(algorithm) ?
                        MessageDigest.getInstance(algorithm, MD4_PROVIDER) : // 通过MessageDigest.getInstance无法直接获取MD4
                        MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException impossible) {
                throw new UndeclaredThrowableException(impossible);
            }
            md.update(seed);
            md.update(passPhrase);
            byte[] result = md.digest();
            if (md.getDigestLength() == 16) { // md4, md5小端法输出结果，无需额外处理
                for (int i = 0; i < 8; i++) {
                    result[i] ^= result[i + 8];
                }
                return Arrays.copyOf(result, 8); // Fold the 128 bit result to 64 bits
            }
            // sha1: Fold the 160 bit result to 64 bits. 规范要求截断前必须用小端法保存数据，SHA默认是大端法
            int[] sha = new int[5];
            for (int i = 0; i < sha.length; i++) {
                sha[i] = (Byte.toUnsignedInt(result[4 * i]) << 24) |
                        (Byte.toUnsignedInt(result[4 * i + 1]) << 16) |
                        (Byte.toUnsignedInt(result[4 * i + 2]) << 8) |
                        Byte.toUnsignedInt(result[4 * i + 3]);
            }
            sha[0] ^= sha[2];
            sha[1] ^= sha[3];
            sha[0] ^= sha[4];
            for (int i = 0, j = 0; j < 8; i++, j += 4) {
                result[j] = (byte) (sha[i] & 0xff);
                result[j + 1] = (byte) ((sha[i] >> 8) & 0xff);
                result[j + 2] = (byte) ((sha[i] >> 16) & 0xff);
                result[j + 3] = (byte) ((sha[i] >> 24) & 0xff);
            }
            return Arrays.copyOf(result, 8);
        }

        private static String bytesToHexStr(byte[] data, String separator) {
            StringBuilder sb = new StringBuilder();
            for (byte b : data) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() < 2) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        }

        /**
         * 将8字节截断的的摘要转换成容易记忆的词语组。
         * 参考<a href="https://sourceforge.net/projects/otp-j2me/">S/Key Generator for J2ME architecture</a>.
         *
         * @param data      截断的摘要
         * @param separator 词组间的分隔符
         * @return 6个词组
         */
        private static String bytesToSixWords(byte[] data, String separator) {
            // 64 bit truncated hash + 2 bit checksum = 6 * 11
            int parity = 0; // The two extra bits in this encoding are used to store a checksum.
            long wi = 0L;
            for (int i = 0; i < 8; i++) {
                wi <<= 8;
                wi |= (data[i] & 0xff);
            }
            long tmp = wi;
            for (int i = 0; i < 64; i += 2) {
                parity += tmp & 0x3;
                tmp >>= 2;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 4; i >= 0; i--) {
                sb.append(atWord((int) ((wi >> (i * 11 + 9)) & 0x7ff)));
                if (separator != null) {
                    sb.append(separator);
                }
            }
            sb.append(atWord((int) ((wi << 2) & 0x7fc) | (parity & 0x03)));
            return sb.toString();
        }

        private static String atWord(int index) {
            return STANDARD_DICTIONARY[0b011_111_111_111 & index];
        }

    }

    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc4226">An HMAC-Based One-Time Password Algorithm</a>
     * 规范定义基于HMAC算法的一次性密码：HOTP(K,C) = Truncate(HMAC-SHA-1(K,C))。
     */
    class HOTP implements OneTimePassword<byte[], Long> {

        static final int[] doubleDigits = {0, 2, 4, 6, 8, 1, 3, 5, 7, 9};

        //                                    0 1  2   3    4     5      6       7        8
        static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

        protected final int codeDigits;

        private final boolean addChecksum;

        private final int truncationOffset;

        public HOTP(int codeDigits, boolean addChecksum, int truncationOffset) {
            this.codeDigits = codeDigits;
            this.addChecksum = addChecksum;
            this.truncationOffset = truncationOffset;
        }

        /**
         * @param secret       私密信息
         * @param movingFactor 服务端随机种子等信息，此处一般为代表移位、时间相关数字
         * @return OTP
         */
        @Override
        public String generate(byte[] secret, Long movingFactor) {
            return generateOTP(secret, movingFactor, codeDigits, addChecksum, truncationOffset);
        }

        private static int calcChecksum(long num, int digits) {
            boolean doubleDigit = true;
            int total = 0;
            while (0 < digits--) {
                int digit = (int) (num % 10);
                num /= 10;
                if (doubleDigit) {
                    digit = doubleDigits[digit];
                }
                total += digit;
                doubleDigit = !doubleDigit;
            }
            int result = total % 10;
            if (result > 0) {
                result = 10 - result;
            }
            return result;
        }

        /**
         * @param crypto   HmacSHA1, HmacSHA256, HmacSHA512
         * @param keyBytes the bytes to use for the HMAC-SHA key
         * @param text     the message or text to be authenticated.
         */
        static byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) {
            try {
                Mac hmac = Mac.getInstance(crypto);
                SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
                hmac.init(macKey);
                return hmac.doFinal(text);
            } catch (GeneralSecurityException gse) {
                throw new UndeclaredThrowableException(gse);
            }
        }

        /**
         * @param secret           the shared secret
         * @param movingFactor     the counter, time, or other value that changes on a per use basis.
         * @param codeDigits       the number of digits in the OTP, not including the checksum, if any.
         * @param addChecksum      a flag that indicates if a checksum digit should be appended to the OTP.
         * @param truncationOffset the offset into the MAC result to begin truncation.
         *                         If this value is out of the range of 0 ... 15, then dynamic truncation  will be used.
         *                         Dynamic truncation is used when the last 4 bits of the last byte of the MAC
         *                         are used to determine the start offset.
         * @return OTP
         */
        public static String generateOTP(byte[] secret, long movingFactor, int codeDigits, boolean addChecksum, int truncationOffset) {
            // put movingFactor value into text byte array
            String result;
            int digits = addChecksum ? (codeDigits + 1) : codeDigits;
            byte[] text = new byte[8];
            for (int i = text.length - 1; i >= 0; i--) {
                text[i] = (byte) (movingFactor & 0xff);
                movingFactor >>= 8;
            }

            // compute hmac hash
            byte[] hash = hmacSha("HmacSHA1", secret, text);

            // put selected bytes into result int
            int offset = hash[hash.length - 1] & 0xf;
            if ((0 <= truncationOffset) &&
                    (truncationOffset < (hash.length - 4))) {
                offset = truncationOffset;
            }
            int binary =
                    ((hash[offset] & 0x7f) << 24)
                            | ((hash[offset + 1] & 0xff) << 16)
                            | ((hash[offset + 2] & 0xff) << 8)
                            | (hash[offset + 3] & 0xff);

            int otp = binary % DIGITS_POWER[codeDigits];
            if (addChecksum) {
                otp = (otp * 10) + calcChecksum(otp, codeDigits);
            }
            result = Integer.toString(otp);
            while (result.length() < digits) {
                result = "0" + result;
            }
            return result;
        }

    }

    class TOTP implements OneTimePassword<String, String> {

        private final String algorithm;

        private final String returnDigits;

        public TOTP(String algorithm, String returnDigits) {
//            super(Integer.decode(returnDigits).intValue(), false, Integer.MIN_VALUE);
            this.algorithm = algorithm;
            this.returnDigits = returnDigits;
        }

        public static String timeAsHex(long time, int step) {
            return Long.toHexString(time / 1000L / step);
        }

        /**
         * @param key  the shared secret, HEX encoded
         * @param time a value that reflects a time
         * @return TOTP
         */
        @Override
        public String generate(String key, String time) {
            return generateTOTP(key, time, returnDigits, algorithm);
        }

        private static byte[] hexStr2Bytes(String hex) {
            // Adding one byte to get the right conversion. Values starting with "0" can be converted
            byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

            // Copy all the REAL bytes, not the "first"
            byte[] ret = new byte[bArray.length - 1];
            System.arraycopy(bArray, 1, ret, 0, ret.length);
            return ret;
        }

        /**
         * This method generates a TOTP value for the given set of parameters.
         *
         * @param key:          the shared secret, HEX encoded
         * @param time:         a value that reflects a time
         * @param returnDigits: number of digits to return
         * @param crypto:       HMAC algorithm，仅支持HmacSHA1, HmacSHA256, HmacSHA512
         * @return a numeric String in base 10 that includes {@link truncationDigits} digits
         */
        public static String generateTOTP(String key, String time, String returnDigits, String crypto) {
            int codeDigits = Integer.decode(returnDigits);
            String result;

            // Using the counter
            // First 8 bytes are for the movingFactor
            // Compliant with base RFC 4226 (HOTP)
            while (time.length() < 16)
                time = "0" + time;

            // Get the HEX in a Byte[]
            byte[] msg = hexStr2Bytes(time);
            byte[] k = hexStr2Bytes(key);
            byte[] hash = HOTP.hmacSha(crypto, k, msg);

            // put selected bytes into result int
            int offset = hash[hash.length - 1] & 0xf;

            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % HOTP.DIGITS_POWER[codeDigits];

            result = Integer.toString(otp);
            while (result.length() < codeDigits) {
                result = "0" + result;
            }
            return result;
        }

    }
}
