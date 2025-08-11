package com.ramendirectory.japanramendirectory.config;

import com.ramendirectory.japanramendirectory.model.*;
import com.ramendirectory.japanramendirectory.repository.*;
import com.ramendirectory.japanramendirectory.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.*;

@Configuration
public class DataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Autowired
    private PrefectureRepository prefectureRepository;
    
    @Autowired
    private AreaRepository areaRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private QueueMethodRepository queueMethodRepository;
    
    @Autowired
    private Environment environment;
    
    @Autowired
    private UserServiceImpl userService; // Inject UserService

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Create initial admin user
            logger.info("Checking for initial admin user...");
            User adminUser = userService.createInitialAdminUser(
                environment.getProperty("admin.username", "admin"),
                environment.getProperty("admin.password", "Admin123!")
            );
            if (adminUser != null) {
                logger.info("Created initial admin user: {}", adminUser.getUsername());
            } else {
                logger.info("Admin user creation skipped (users already exist)");
            }

            // Check if database is already populated
            if (prefectureRepository.count() > 0) {
                logger.info("Database already contains data, skipping initialization");
                return;
            }
            
            logger.info("Starting database initialization...");
            
            // Initialize prefectures
            Map<String, Prefecture> prefectureMap = initializePrefectures();
            
            // Initialize areas within prefectures
            Map<String, Area> areaMap = initializeAreas(prefectureMap);
            
            // Initialize some sample restaurants
            initializeRestaurants(areaMap);
            
            logger.info("Database initialization completed successfully");
        };
    }
    
    private Map<String, Prefecture> initializePrefectures() {
        logger.info("Initializing prefectures...");
        Map<String, Prefecture> prefectureMap = new HashMap<>();
        
        createPrefecture("東京都", "Tokyo", prefectureMap);
        createPrefecture("大阪府", "Osaka", prefectureMap);
        createPrefecture("京都府", "Kyoto", prefectureMap);
        createPrefecture("福岡縣", "Fukuoka", prefectureMap);
        createPrefecture("北海道", "Hokkaido", prefectureMap);
        
        logger.info("Created {} prefectures", prefectureMap.size());
        return prefectureMap;
    }
    
    private void createPrefecture(String name, String nameInEnglish, Map<String, Prefecture> prefectureMap) {
        Prefecture prefecture = new Prefecture();
        prefecture.setName(name);
        prefecture.setNameInEnglish(nameInEnglish);
        prefecture = prefectureRepository.save(prefecture);
        prefectureMap.put(name, prefecture);
    }
    
    private Map<String, Area> initializeAreas(Map<String, Prefecture> prefectureMap) {
        logger.info("Initializing areas...");
        Map<String, Area> areaMap = new HashMap<>();
        
        Prefecture tokyo = prefectureMap.get("東京都");
        createArea("千代田區", "Chiyoda", tokyo, areaMap);
        createArea("中央區", "Chuo", tokyo, areaMap);
        createArea("港區", "Minato", tokyo, areaMap);
        createArea("新宿區", "Shinjuku", tokyo, areaMap);
        createArea("文京區", "Bunkyo", tokyo, areaMap);
        createArea("台東區", "Taito", tokyo, areaMap);
        createArea("墨田區", "Sumida", tokyo, areaMap);
        createArea("江東區", "Koto", tokyo, areaMap);
        createArea("品川區", "Shinagawa", tokyo, areaMap);
        createArea("目黑區", "Meguro", tokyo, areaMap);
        createArea("大田區", "Ota", tokyo, areaMap);
        createArea("世田谷區", "Setagaya", tokyo, areaMap);
        createArea("渋谷區", "Shibuya", tokyo, areaMap);
        createArea("中野區", "Nakano", tokyo, areaMap);
        createArea("杉並區", "Suginami", tokyo, areaMap);
        createArea("豊島區", "Toshima", tokyo, areaMap);
        createArea("北區", "Kita", tokyo, areaMap);
        createArea("荒川區", "Arakawa", tokyo, areaMap);
        createArea("板橋區", "Itabashi", tokyo, areaMap);
        createArea("練馬區", "Nerima", tokyo, areaMap);
        createArea("足立區", "Adachi", tokyo, areaMap);
        createArea("葛飾區", "Katsushika", tokyo, areaMap);
        createArea("江戶川區", "Edogawa", tokyo, areaMap);
        
        Prefecture osaka = prefectureMap.get("大阪府");
        createArea("梅田", "Umeda", osaka, areaMap);
        createArea("難波", "Namba", osaka, areaMap);
        createArea("天王寺", "Tennoji", osaka, areaMap);
        
        Prefecture kyoto = prefectureMap.get("京都府");
        createArea("中京區", "Nakagyo", kyoto, areaMap);
        createArea("東山區", "Higashiyama", kyoto, areaMap);
        
        Prefecture fukuoka = prefectureMap.get("福岡縣");
        createArea("博多區", "Hakata", fukuoka, areaMap);
        createArea("中央區", "Chuo", fukuoka, areaMap);
        
        Prefecture hokkaido = prefectureMap.get("北海道");
        createArea("札幌市", "Sapporo", hokkaido, areaMap);
        createArea("函館市", "Hakodate", hokkaido, areaMap);
        
        logger.info("Created {} areas", areaMap.size());
        return areaMap;
    }
    
    private void createArea(String name, String nameInEnglish, Prefecture prefecture, Map<String, Area> areaMap) {
        Area area = new Area();
        area.setName(name);
        area.setNameInEnglish(nameInEnglish);
        area.setPrefecture(prefecture);
        area = areaRepository.save(area);
        areaMap.put(prefecture.getName() + "-" + name, area);
    }
    
    private void initializeRestaurants(Map<String, Area> areaMap) {
        logger.info("Initializing restaurants...");
        
        Calendar calendar = Calendar.getInstance();
        
        // 麵屋一燈
        calendar.set(2006, Calendar.JANUARY, 17);
        createRestaurant(
            "麺屋吉左右",
            areaMap.get("東京都-江東區"),
            "東陽1-11-3",
            null,
            null,
            null,
            "135-0016",
            99.7,
            new HashSet<>(Arrays.asList(Genre.TSUKEMEN, Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.豚骨, SoupBase.魚介)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "於食劵機購買食劵後排隊等候 \n- 高峰時段約20-30人等候\n- 參考等候時間：20-45分鐘"),
            9,
            null,
            "拉麵（麵200g） 1200円\n味玉拉麵 1300円\n大份拉麵（麵300g） 1300円\n大份拉麵味玉 1400円\n沾麵（麵300g） 1200円\n味玉沾麵 1300円\n大份沾麵（麵450g） 1350円\n大份沾麵味玉 1500円\n※份量較多，可調整減少\n・加料\n海苔 130円\n筍乾 250円\n叉燒（3片） 500円 ※使用國產肩里肌\n・飲料\n惠比壽啤酒（小瓶） 480円\n",
            "11:30-15:00",
            "每週三，週五，週日",
            calendar.getTime(),
            "麵屋吉左右位於東京木場站附近，步行約5分鐘，是一家屢獲殊榮的拉麵名店，連續五年入選食べログ百名店，並榮獲2022-2023年TRY拉麵大賞名店MIX部門獎項。該店以其濃郁的魚介豚骨つけ麵（沾麵）聞名，湯頭融合雞骨、豬骨與多種魚介食材，呈現濃厚卻不過膩的風味，搭配中粗直麵，口感彈牙且順滑。店內環境整潔，服務親切，適合拉麵愛好者前來朝聖。惟需注意，店外常有排隊人潮，建議避開尖峰時段或做好等待準備。"
        );
        
        // 拉麵林田
        calendar.set(2022, Calendar.DECEMBER, 11);
        createRestaurant(
            "Tokyo Style Noodle ほたて日和",
            areaMap.get("東京都-渋谷區"),
            "神田佐久間町2-25",
            "梅屋ビル",
            "1階",
            null,
            "101-0025",
            99.5,
            new HashSet<>(Arrays.asList(Genre.RAMEN,Genre.TSUKEMEN)),
            new HashSet<>(Arrays.asList(SoupBase.醬油, SoupBase.鹽味)),
            false,
            createQueueMethod(QueueType.TICKET, ""),
            15,
            createSocialMediaLinks("https://twitter.com/hayashida_ramen", null),
            "特製醬油拉麵 1100元\n醬油拉麵 850元\n溏心蛋拉麵 950元",
            "10:00-22:00",
            "週一和第三個週二",
            calendar.getTime(),
            "「拉麵林田」是位於渋谷區的一家知名醬油拉麵店。自2012年開業以來，以其精細的製作和對食材的執著吸引了許多粉絲。" +
            "招牌菜「特製醬油拉麵」採用雞肉和豬肉的雙湯底，加入海鮮風味，形成了深邃的味道。" +
            "特別是自製麵條含水率較高，口感彈牙。叉燒採用低溫烹調，柔軟得令人驚嘆。" +
            "店內氛圍安靜舒適，適合單人用餐。以吧檯座位為主，可以觀看拉麵製作過程。" +
            "作為熱門店，特別是午餐時段常有排隊。夜間較晚時段相對不那麼擁擠。"
        );
        
        // 金久右衛門
        calendar.set(2005, Calendar.NOVEMBER, 20);
        createRestaurant(
            "金久右衛門",
            areaMap.get("大阪府-難波"),
            "難波中2-4-17",
            null,
            null,
            null,
            "542-0076",
            79.0,
            new HashSet<>(Arrays.asList(Genre.TSUKEMEN)),
            new HashSet<>(Arrays.asList(SoupBase.豚骨)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，翻桌率較高，平均等待時間約15-20分鐘。觀光客多的時段可能需要更久。"),
            22,
            null,
            "金久右衛門拉麵 880元\n特製拉麵 1180元\n叉燒拉麵 1280元",
            "10:00-22:00",
            "不定休",
            calendar.getTime(),
            "「金久右衛門」是大阪難波長期深受喜愛的豬骨拉麵名店。自2005年創業以來，以濃厚但後味清爽的豬骨湯底廣受歡迎。" +
            "特色是將豬骨和雞骨長時間熬煮而成的濃白湯底，搭配適中硬度的中細麵條，形成絕妙平衡。" +
            "配料中的叉燒肉厚切，經過長時間慢煮，味道深厚。" +
            "店內氣氛活躍，設有吧檯座位和桌席。大阪式的熱情服務也是人氣原因之一。" +
            "雖然有很多遊客光顧，但由於翻桌率高，等待時間相對較短。想品嚐大阪代表性的豬骨拉麵，這裡是必訪之處。"
        );
        
        // 新增更多測試餐廳
        
        // 一蘭拉麵
        calendar.set(1960, Calendar.APRIL, 15);
        createRestaurant(
            "一蘭 新宿中央東口店",
            areaMap.get("東京都-新宿區"),
            "新宿3-34-11",
            "ピースビル B1F",
            null,
            null,
            "160-0022",
            78.5,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.豚骨)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，24小時營業，尖峰時段等待時間30-60分鐘"),
            32,
            createSocialMediaLinks("https://twitter.com/ICHIRAN_PR", "https://www.instagram.com/ichiran_ramen/"),
            "一蘭拉麵 890元\n味玉拉麵 980元\n叉燒拉麵 1200元\n一蘭特製拉麵 1320元",
            "24小時營業",
            "無休",
            calendar.getTime(),
            "一蘭是源自福岡的國際知名豬骨拉麵連鎖店，以獨特的「味集中」隔間座位和客製化風味系統而聞名。其招牌豬骨湯底濃郁但不過於油膩，搭配細麵條呈現經典九州風味。特別的點餐系統允許顧客精確調整湯的濃度、辣度和蒜味等，提供個人化的用餐體驗。新宿店位於交通便利的地點，24小時營業，是品嚐正宗博多豬骨拉麵的理想選擇。"
        );
        
        // 蔦
        calendar.set(2012, Calendar.OCTOBER, 10);
        createRestaurant(
            "Japanese Soba Noodles 蔦",
            areaMap.get("東京都-杉並區"),
            "南荻窪3-14-4",
            null,
            null,
            null,
            "167-0052",
            99.8,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.醬油, SoupBase.魚介)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "需提前排隊，開店前1-2小時已有人等候"),
            10,
            null,
            "醬油拉麵 1000円\n醬油特製拉麵 1300円\n貝類醬油拉麵 1100円\n貝類特製拉麵 1400円",
            "11:00-16:00",
            "週三和週四",
            calendar.getTime(),
            "「蔦」是東京杉並區的米其林星級拉麵店，由大橋拓也主廚創立，自2015年起連續獲得米其林一星榮耀。其招牌醬油拉麵結合雞、蛤蜊和海鮮元素，創造出深邃複雜的湯底。麵條手工製作，質地完美。店內僅有10個座位，常有長時間排隊。作為拉麵界的傳奇，它代表了日本拉麵藝術的巔峰成就。"
        );
        
        // 中華そば 葉山
        calendar.set(2010, Calendar.APRIL, 3);
        createRestaurant(
            "中華そば 葉山",
            areaMap.get("北海道-函館市"),
            "宮前町9-11",
            null,
            null,
            null,
            "040-0008",
            87.6,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.醬油)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，通常需要等候15-30分鐘"),
            16,
            null,
            "中華そば 750円\n特製中華そば 1050円\n肉中華そば 950円\n特製肉中華そば 1250円",
            "11:00-15:00, 17:30-20:00",
            "週二",
            calendar.getTime(),
            "「中華そば 葉山」是函館知名的醬油拉麵專門店，以其澄清的雞骨湯底聞名。經過細緻熬煮的清湯充滿了雞肉的鮮甜與獨特香氣，搭配傳統細直麵條，呈現出經典的北海道風味。店內裝潢簡樸溫馨，座位不多但氣氛舒適。由於其在當地的高人氣，午餐和晚餐時段常有排隊，建議避開用餐高峰期。"
        );
        
        // 函館麺厨房 あじさい
        calendar.set(2001, Calendar.JULY, 20);
        createRestaurant(
            "函館麺厨房 あじさい 本店",
            areaMap.get("北海道-函館市"),
            "美原1-16-12",
            null,
            null,
            null,
            "041-0806",
            83.2,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.鹽味)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, ""),
            20,
            null,
            "塩らーめん 780円\n特製塩らーめん 1050円\nあじさい塩らーめん 1150円\nバターコーン塩らーめん 890円",
            "11:00-21:00",
            "週三",
            calendar.getTime(),
            "「函館麺厨房 あじさい」是函館著名的鹽味拉麵店，以淡雅精緻的湯頭和豐富配料聞名。創業於2001年，已成為當地代表性拉麵店之一。特別之處在於使用函館當地水產和蔬菜製作的清爽鹽味湯底，搭配中細麵，呈現出北海道特有的風味。店內提供多種變化版本，其中「特製鹽拉麵」加入蛋、叉燒和蔬菜，成為人氣之選。"
        );
        
        // 麺屋武蔵
        calendar.set(1996, Calendar.MARCH, 10);
        createRestaurant(
            "麺屋武蔵 本店",
            areaMap.get("東京都-台東區"),
            "上野3-2-2",
            null,
            null,
            null,
            "110-0005",
            98.8,
            new HashSet<>(Arrays.asList(Genre.RAMEN, Genre.TSUKEMEN)),
            new HashSet<>(Arrays.asList(SoupBase.魚介)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，尖峰時段需等待30-45分鐘"),
            12,
            createSocialMediaLinks("https://twitter.com/menya_musashi", "https://www.instagram.com/menmusashiofficial/"),
            "武蔵ら〜麺 950円\n武藏つけ麺 980円\n濃厚魚介ら〜めん 1050円\n特製極め炙りチャーシューめん 1380円",
            "11:00-22:00",
            "無休",
            calendar.getTime(),
            "「麺屋武蔵」是東京知名拉麵品牌，本店創立於1996年，以獨特的魚介系湯底和粗壯麵條聞名。其標志性特徵是融合了豬骨和多種海鮮熬製的濃厚湯底，呈現出層次分明的鮮香味道。店內特色包括可選粗麵，以及豐富的配料選擇，特別是厚切叉燒深受歡迎。由於聲譽卓著，常有排隊現象，但快速的服務節奏使等待時間不會太長。"
        );
        
        // 六厘舎
        calendar.set(2005, Calendar.AUGUST, 15);
        createRestaurant(
            "東京豚骨拉麺 ばんから 北千住店",
            areaMap.get("東京都-北區"),
            "北区志茂1-2-8",
            null,
            null,
            null,
            "114-0001",
            98.9,
            new HashSet<>(Arrays.asList(Genre.RAMEN, Genre.TSUKEMEN)),
            new HashSet<>(Arrays.asList(SoupBase.魚介, SoupBase.醬油)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，尖峰時段需等待20-40分鐘"),
            20,
            createSocialMediaLinks("https://twitter.com/bankara1988", null),
            "豚骨らーめん 830円\n特製豚骨らーめん 1080円\n焦がし豚骨らーめん 880円\n特製焦がし豚骨らーめん 1130円",
            "11:00-深夜01:00",
            "不定休",
            calendar.getTime(),
            "「東京豚骨拉麺 ばんから」是一家以濃厚的豚骨湯底聞名的拉麵店。其標誌性的「焦がし豚骨らーめん」（焦香豚骨拉麵）特別受到歡迎，湯底經過長時間熬煮，最後加入特殊焦香醬油提升風味層次。麵條採用中等粗細，硬度適中，搭配豐富的配料，包括厚切叉燒、溏心蛋和脆嫩的竹筍片。北千住店位置方便，店鋪空間雖不寬敞但座位配置合理，有效提高了用餐效率。"
        );
        
        // 風雲児
        calendar.set(2009, Calendar.SEPTEMBER, 1);
        createRestaurant(
            "らぁ麺 風雲児",
            areaMap.get("東京都-杉並區"),
            "西荻北3-21-13",
            null,
            null,
            null,
            "164-0001",
            97.2,
            new HashSet<>(Arrays.asList(Genre.TSUKEMEN, Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.豚骨, SoupBase.醬油)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，尖峰時段需等待15-30分鐘"),
            12,
            createSocialMediaLinks("https://twitter.com/fuunji_ramen", null),
            "らぁ麺 850円\n特製らぁ麺 1150円\nつけ麺 900円\n特製つけ麺 1200円",
            "11:30-15:00, 18:00-21:00",
            "週一和週二",
            calendar.getTime(),
            "「らぁ麺 風雲児」是位於杉並區的人氣拉麵店，以其濃厚的豚骨魚介湯底和特色沾麵而聞名。開業於2009年，融合了多種日本拉麵風格。特別之處在於其濃縮的沾麵湯汁，結合了豬骨的濃郁與海鮮的鮮美，搭配粗壯有彈性的麵條，創造出獨特的口感體驗。店內裝潢簡潔，座位有限，常常需要排隊等候，但快速的服務使整體等待時間不會過長。"
        );
        
        // 無敵家
        calendar.set(2001, Calendar.JUNE, 25);
        createRestaurant(
            "らーめん 無敵家",
            areaMap.get("東京都-八王子市"),
            "八幡町13-6",
            null,
            null,
            null,
            "192-0085",
            93.1,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.豚骨, SoupBase.魚介)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，平均等待時間10-20分鐘"),
            18,
            null,
            "らーめん 750円\n特製らーめん 1050円\nトッピング盛りらーめん 1250円\n塩らーめん 750円",
            "11:00-20:00",
            "週三",
            calendar.getTime(),
            "「らーめん 無敵家」是八王子市的知名拉麵店，以其獨特的濃厚豚骨魚介湯底聞名。成立於2001年，多年來一直保持著穩定的人氣。特色在於將豚骨的濃郁與魚介的鮮美完美融合，湯底醇厚但不過膩，搭配中細直麵，呈現出層次豐富的口感。店內裝潢簡約實用，座位安排緊湊但舒適。由於店家聲譽良好，常有當地居民和拉麵愛好者慕名而來。"
        );
        
        // 東京駒形
        calendar.set(2011, Calendar.APRIL, 8);
        createRestaurant(
            "中華麵酒家 東京駒形",
            areaMap.get("東京都-新宿區"),
            "西新宿7-18-11",
            "プリマベーラ西新宿",
            "2階",
            null,
            "160-0022",
            95.7,
            new HashSet<>(Arrays.asList(Genre.RAMEN)),
            new HashSet<>(Arrays.asList(SoupBase.醬油)),
            false,
            createQueueMethod(QueueType.QUEUE_UP, "店外排隊，平均等待時間10-20分鐘"),
            14,
            null,
            "醤油ら〜めん 900円\n特製醤油ら〜めん 1250円\n限定ブラック醤油ら〜めん 950円\n特製限定ブラック醤油ら〜めん 1300円",
            "11:30-15:00, 17:30-21:00",
            "週二",
            calendar.getTime(),
            "「中華麵酒家 東京駒形」是新宿區的高評價醬油拉麵店，以精緻的醬油湯底和細膩的手工麵條受到好評。創立於2011年，店內融合了日式和中華風格的裝潢，營造出溫馨舒適的氛圍。特色料理「特製醬油拉麵」採用秘傳醬油配方，湯頭清澈卻味道深厚，配以細柔有彈性的麵條，以及厚切叉燒和完美煮製的溏心蛋，呈現出平衡的美味。位於新宿繁華區域，交通便利但店內環境安靜，成為當地美食愛好者的熱門選擇。"
        );
        
        logger.info("Created restaurants for testing pagination");
    }
    
    private void createRestaurant(
            String name,
            Area area,
            String detailedAddress,
            String building,
            String floor,
            String unit,
            String postalCode,
            Double score,
            Set<Genre> genres,
            Set<SoupBase> soupBases,
            Boolean reservationSystem,
            QueueMethod queueMethod,
            Integer seats,
            Map<String, String> socialMediaLinks,
            String menuContent,
            String openingHours,
            String restDay,
            Date openingDate,
            String descriptionContent) {
        
        // Check that area is not null before proceeding
        if (area == null) {
            logger.error("Cannot create restaurant '{}': Area cannot be null", name);
            return;
        }
        
        Address address = new Address();
        address.setArea(area);
        address.setDetailedAddress(detailedAddress);
        address.setBuilding(building);
        address.setFloor(floor);
        address.setUnit(unit);
        address.setPostalCode(postalCode);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setGenres(genres);
        restaurant.setSoupBases(soupBases);
        restaurant.setScore(score);
        restaurant.setReservationSystem(reservationSystem);
        restaurant.setQueueMethod(queueMethod);
        restaurant.setAddress(address);
        restaurant.setSeats(seats);
        restaurant.setSocialMediaLinks(socialMediaLinks);
        restaurant.setOpeningHours(openingHours);
        restaurant.setRestDay(restDay);
        restaurant.setOpeningDate(openingDate);
        
        // Save restaurant first
        restaurant = restaurantRepository.save(restaurant);
        
        // Create and save menu if menuContent is provided
        if (menuContent != null && !menuContent.trim().isEmpty()) {
            Menu menu = new Menu();
            menu.setRestaurant(restaurant);
            menu.setMenuContent(menuContent);
            menu.setLastUpdated(new Date());
            
            // Set menu relationship on restaurant
            restaurant.setMenu(menu);
        }
        
        // Create and save description if descriptionContent is provided
        if (descriptionContent != null && !descriptionContent.trim().isEmpty()) {
            RestaurantDescription description = new RestaurantDescription();
            description.setRestaurant(restaurant);
            description.setContent(descriptionContent);
            description.setLastUpdated(new Date());
            
            // Set description relationship on restaurant
            restaurant.setDescription(description);
        }
        
        // Save again to update the relationships
        restaurantRepository.save(restaurant);
    }
    
    private Map<String, String> createSocialMediaLinks(String twitter, String instagram) {
        if (twitter == null && instagram == null) {
            return null;
        }
        
        Map<String, String> links = new HashMap<>();
        if (twitter != null) {
            links.put("Twitter", twitter);
        }
        if (instagram != null) {
            links.put("Instagram", instagram);
        }
        return links;
    }
    
    private QueueMethod createQueueMethod(QueueType type, String detailedGuide) {
        QueueMethod queueMethod = new QueueMethod();
        queueMethod.setType(type);
        queueMethod.setDetailedGuide(detailedGuide);
        return queueMethodRepository.save(queueMethod);
    }
}