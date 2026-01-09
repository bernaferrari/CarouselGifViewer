package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseDataSource(
    private val delayMs: Long = 500
) : DictDataSource {

    override fun getItems(): Flow<List<GifItem>> = flow {
        delay(delayMs)
        emit(
            listOf(
                GifItem(
                    id = "1",
                    title = "1.Maria Madalena",
                    videoUrl = "https://media4.giphy.com/media/qcUAiEliTVGbQd039p/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/qcUAiEliTVGbQd039p/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/qcUAiEliTVGbQd039p"
                ),
                GifItem(
                    id = "2",
                    title = "2.Lázaro",
                    videoUrl = "https://media4.giphy.com/media/FpeL03GzEhH1ow5GBO/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/FpeL03GzEhH1ow5GBO/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/FpeL03GzEhH1ow5GBO"
                ),
                GifItem(
                    id = "3",
                    title = "3.Revelação",
                    videoUrl = "https://media4.giphy.com/media/jTCdGp4Z2uOgQAPaZ4/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/jTCdGp4Z2uOgQAPaZ4/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/jTCdGp4Z2uOgQAPaZ4"
                ),
                GifItem(
                    id = "4",
                    title = "4.Canaã",
                    videoUrl = "https://media4.giphy.com/media/mRo5TieakiMcZsY6iG/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mRo5TieakiMcZsY6iG/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mRo5TieakiMcZsY6iG"
                ),
                GifItem(
                    id = "5",
                    title = "5.Discípulo",
                    videoUrl = "https://media4.giphy.com/media/hnfFtE7KQLdCb0urIM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/hnfFtE7KQLdCb0urIM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/hnfFtE7KQLdCb0urIM"
                ),
                GifItem(
                    id = "6",
                    title = "6.Antigo Testamento",
                    videoUrl = "https://media4.giphy.com/media/f082uZWzQH9Uy4Wrku/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/f082uZWzQH9Uy4Wrku/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/f082uZWzQH9Uy4Wrku"
                ),
                GifItem(
                    id = "7",
                    title = "7.Amém",
                    videoUrl = "https://media4.giphy.com/media/50DRfUVl9QJSZJIcxM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/50DRfUVl9QJSZJIcxM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/50DRfUVl9QJSZJIcxM"
                ),
                GifItem(
                    id = "8",
                    title = "8.Alma",
                    videoUrl = "https://media4.giphy.com/media/5igRofxTLyMJNTDNt8/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/5igRofxTLyMJNTDNt8/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/5igRofxTLyMJNTDNt8"
                ),
                GifItem(
                    id = "9",
                    title = "9.Abençoar",
                    videoUrl = "https://media4.giphy.com/media/pKASxxIxeWhByXuLvu/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/pKASxxIxeWhByXuLvu/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/pKASxxIxeWhByXuLvu"
                ),
                GifItem(
                    id = "10",
                    title = "10.Apóstolos",
                    videoUrl = "https://media4.giphy.com/media/2i64oSLWrmup9mBdiq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/2i64oSLWrmup9mBdiq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/2i64oSLWrmup9mBdiq"
                ),
                GifItem(
                    id = "11",
                    title = "11.Acreditar",
                    videoUrl = "https://media4.giphy.com/media/zjYfiqsmap5rYWj96e/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/zjYfiqsmap5rYWj96e/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/zjYfiqsmap5rYWj96e"
                ),
                GifItem(
                    id = "12",
                    title = "12.Adoração",
                    videoUrl = "https://media4.giphy.com/media/nAT5Jun71Wef0JimOU/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/nAT5Jun71Wef0JimOU/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/nAT5Jun71Wef0JimOU"
                ),
                GifItem(
                    id = "13",
                    title = "13.Aleluia",
                    videoUrl = "https://media4.giphy.com/media/8T2naYzd39YKHXEHEW/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/8T2naYzd39YKHXEHEW/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/8T2naYzd39YKHXEHEW"
                ),
                GifItem(
                    id = "14",
                    title = "14.Antioquia",
                    videoUrl = "https://media4.giphy.com/media/mvXTUZJ05sHTDHTgTi/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mvXTUZJ05sHTDHTgTi/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mvXTUZJ05sHTDHTgTi"
                ),
                GifItem(
                    id = "15",
                    title = "15.Apolo",
                    videoUrl = "https://media4.giphy.com/media/tlWzy5ISJ7DTCDTMZS/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/tlWzy5ISJ7DTCDTMZS/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/tlWzy5ISJ7DTCDTMZS"
                ),
                GifItem(
                    id = "16",
                    title = "16.Bíblia",
                    videoUrl = "https://media4.giphy.com/media/DvgPMjlghbWjOqRODd/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/DvgPMjlghbWjOqRODd/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/DvgPMjlghbWjOqRODd"
                ),
                GifItem(
                    id = "17",
                    title = "17.Arrebatar",
                    videoUrl = "https://media4.giphy.com/media/3IMuYcBXvHiSGtzskx/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/3IMuYcBXvHiSGtzskx/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/3IMuYcBXvHiSGtzskx"
                ),
                GifItem(
                    id = "18",
                    title = "18.Babel",
                    videoUrl = "https://media4.giphy.com/media/dbnHGB92DaQZ3DAqIO/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/dbnHGB92DaQZ3DAqIO/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/dbnHGB92DaQZ3DAqIO"
                ),
                GifItem(
                    id = "19",
                    title = "19.Baal",
                    videoUrl = "https://media4.giphy.com/media/lB4iEoJBo7z0lef0eq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/lB4iEoJBo7z0lef0eq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/lB4iEoJBo7z0lef0eq"
                ),
                GifItem(
                    id = "20",
                    title = "20.Árabe",
                    videoUrl = "https://media4.giphy.com/media/e51R7GdDRlNYLgaWJ1/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/e51R7GdDRlNYLgaWJ1/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/e51R7GdDRlNYLgaWJ1"
                ),
                GifItem(
                    id = "21",
                    title = "21.Ascenção",
                    videoUrl = "https://media4.giphy.com/media/aPUlL7itaoK0crBBmL/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aPUlL7itaoK0crBBmL/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aPUlL7itaoK0crBBmL"
                ),
                GifItem(
                    id = "22",
                    title = "22.Batizar",
                    videoUrl = "https://media4.giphy.com/media/qMqy6zugfFgYaGn4Dt/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/qMqy6zugfFgYaGn4Dt/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/qMqy6zugfFgYaGn4Dt"
                ),
                GifItem(
                    id = "23",
                    title = "23.Arca da Aliança",
                    videoUrl = "https://media4.giphy.com/media/qtKvrNMtsAQp24xpgp/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/qtKvrNMtsAQp24xpgp/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/qtKvrNMtsAQp24xpgp"
                ),
                GifItem(
                    id = "24",
                    title = "24.Babilônia",
                    videoUrl = "https://media4.giphy.com/media/sCnT3IF1NQlje2a2yY/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/sCnT3IF1NQlje2a2yY/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/sCnT3IF1NQlje2a2yY"
                ),
                GifItem(
                    id = "25",
                    title = "25.Beréia",
                    videoUrl = "https://media4.giphy.com/media/5kZjzDtOVzejVPmXN5/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/5kZjzDtOVzejVPmXN5/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/5kZjzDtOVzejVPmXN5"
                ),
                GifItem(
                    id = "26",
                    title = "26.Belém",
                    videoUrl = "https://media4.giphy.com/media/q4ft3h8mzvqsuBva3g/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/q4ft3h8mzvqsuBva3g/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/q4ft3h8mzvqsuBva3g"
                ),
                GifItem(
                    id = "27",
                    title = "27.Arrepender-se",
                    videoUrl = "https://media4.giphy.com/media/fcVE6dB8ULqicXOfQN/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/fcVE6dB8ULqicXOfQN/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/fcVE6dB8ULqicXOfQN"
                ),
                GifItem(
                    id = "28",
                    title = "28.Cesareia Marítima",
                    videoUrl = "https://media4.giphy.com/media/va1fW48aEC7uwBiVGe/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/va1fW48aEC7uwBiVGe/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/va1fW48aEC7uwBiVGe"
                ),
                GifItem(
                    id = "29",
                    title = "29.Ceifar",
                    videoUrl = "https://media4.giphy.com/media/QTO2WJJKDNmhLmlgiI/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QTO2WJJKDNmhLmlgiI/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QTO2WJJKDNmhLmlgiI"
                ),
                GifItem(
                    id = "30",
                    title = "30.Céu",
                    videoUrl = "https://media4.giphy.com/media/RkzvlqNUmUj15TJvik/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/RkzvlqNUmUj15TJvik/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/RkzvlqNUmUj15TJvik"
                ),
                GifItem(
                    id = "31",
                    title = "31.Candelabro",
                    videoUrl = "https://media4.giphy.com/media/v2bfGgaBbENmK4D1Kb/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/v2bfGgaBbENmK4D1Kb/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/v2bfGgaBbENmK4D1Kb"
                ),
                GifItem(
                    id = "32",
                    title = "32.Chipre",
                    videoUrl = "https://media4.giphy.com/media/CQZTgp21fVdzNdTIoM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CQZTgp21fVdzNdTIoM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CQZTgp21fVdzNdTIoM"
                ),
                GifItem(
                    id = "33",
                    title = "33.Caná",
                    videoUrl = "https://media4.giphy.com/media/jiSqQM5Mx3YELTPqzb/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/jiSqQM5Mx3YELTPqzb/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/jiSqQM5Mx3YELTPqzb"
                ),
                GifItem(
                    id = "34",
                    title = "34.Carpinteiro",
                    videoUrl = "https://media4.giphy.com/media/DGFgyD485hVdZRKsHu/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/DGFgyD485hVdZRKsHu/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/DGFgyD485hVdZRKsHu"
                ),
                GifItem(
                    id = "35",
                    title = "35.Ceia",
                    videoUrl = "https://media4.giphy.com/media/sptIuxqd8z5wdzrs6J/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/sptIuxqd8z5wdzrs6J/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/sptIuxqd8z5wdzrs6J"
                ),
                GifItem(
                    id = "36",
                    title = "36.Capítulo",
                    videoUrl = "https://media4.giphy.com/media/4WX4A6loiSEYMA6fYZ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/4WX4A6loiSEYMA6fYZ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/4WX4A6loiSEYMA6fYZ"
                ),
                GifItem(
                    id = "37",
                    title = "37.Ceia",
                    videoUrl = "https://media4.giphy.com/media/PhYdKAHNWY8yO0Gn7I/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/PhYdKAHNWY8yO0Gn7I/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/PhYdKAHNWY8yO0Gn7I"
                ),
                GifItem(
                    id = "38",
                    title = "38.Calvário",
                    videoUrl = "https://media4.giphy.com/media/QhLQo5SvjtHJTJa7Sx/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QhLQo5SvjtHJTJa7Sx/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QhLQo5SvjtHJTJa7Sx"
                ),
                GifItem(
                    id = "39",
                    title = "39.Compaixão",
                    videoUrl = "https://media4.giphy.com/media/QVo4R9gY7sVTtMcn0g/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QVo4R9gY7sVTtMcn0g/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QVo4R9gY7sVTtMcn0g"
                ),
                GifItem(
                    id = "40",
                    title = "40.Dízimo",
                    videoUrl = "https://media4.giphy.com/media/5YQfC2GqFr8EWqUig3/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/5YQfC2GqFr8EWqUig3/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/5YQfC2GqFr8EWqUig3"
                ),
                GifItem(
                    id = "2096",
                    title = "41.Confessar",
                    videoUrl = "https://media4.giphy.com/media/Y6rqA8szciynzB12Cs/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Y6rqA8szciynzB12Cs/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Y6rqA8szciynzB12Cs"
                ),
                GifItem(
                    id = "2097",
                    title = "42.Confiar",
                    videoUrl = "https://media4.giphy.com/media/u8K8RKlCAYavbgmLSc/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/u8K8RKlCAYavbgmLSc/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/u8K8RKlCAYavbgmLSc"
                ),
                GifItem(
                    id = "2098",
                    title = "43.Diácono",
                    videoUrl = "https://media4.giphy.com/media/mHolycWWKN2qtLJNUW/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mHolycWWKN2qtLJNUW/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mHolycWWKN2qtLJNUW"
                ),
                GifItem(
                    id = "2099",
                    title = "44.Crente",
                    videoUrl = "https://media4.giphy.com/media/Mn9XOWMQH1n7t6fFPq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Mn9XOWMQH1n7t6fFPq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Mn9XOWMQH1n7t6fFPq"
                ),
                GifItem(
                    id = "2100",
                    title = "45.Digno",
                    videoUrl = "https://media4.giphy.com/media/1ZKphQcfp3i6mpvQjN/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/1ZKphQcfp3i6mpvQjN/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/1ZKphQcfp3i6mpvQjN"
                ),
                GifItem(
                    id = "2101",
                    title = "46.Criar",
                    videoUrl = "https://media4.giphy.com/media/T3tNOCUwpHjjrPksow/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/T3tNOCUwpHjjrPksow/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/T3tNOCUwpHjjrPksow"
                ),
                GifItem(
                    id = "2102",
                    title = "47.Doutrina",
                    videoUrl = "https://media4.giphy.com/media/fXpt6oA6WkWAMb1DyM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/fXpt6oA6WkWAMb1DyM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/fXpt6oA6WkWAMb1DyM"
                ),
                GifItem(
                    id = "2103",
                    title = "48.Criador",
                    videoUrl = "https://media4.giphy.com/media/O5XEZ3oDZ6nTQITUFq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/O5XEZ3oDZ6nTQITUFq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/O5XEZ3oDZ6nTQITUFq"
                ),
                GifItem(
                    id = "2104",
                    title = "49.Culto",
                    videoUrl = "https://media4.giphy.com/media/zc5ImIu9WJozi1QLop/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/zc5ImIu9WJozi1QLop/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/zc5ImIu9WJozi1QLop"
                ),
                GifItem(
                    id = "2105",
                    title = "50.Cruz",
                    videoUrl = "https://media4.giphy.com/media/7RbrA4kWWZ4N3fCb8o/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/7RbrA4kWWZ4N3fCb8o/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/7RbrA4kWWZ4N3fCb8o"
                ),
                GifItem(
                    id = "2106",
                    title = "51.Dez Mandamentos",
                    videoUrl = "https://media4.giphy.com/media/MsE9Vlx8pdNlYt2Ixm/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/MsE9Vlx8pdNlYt2Ixm/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/MsE9Vlx8pdNlYt2Ixm"
                ),
                GifItem(
                    id = "2107",
                    title = "52.Cristão",
                    videoUrl = "https://media4.giphy.com/media/GXbeODAaOdkvL7Oe0w/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/GXbeODAaOdkvL7Oe0w/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/GXbeODAaOdkvL7Oe0w"
                ),
                GifItem(
                    id = "2108",
                    title = "53.Descendente",
                    videoUrl = "https://media4.giphy.com/media/AfZT6ODUP7gsuPLHru/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/AfZT6ODUP7gsuPLHru/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/AfZT6ODUP7gsuPLHru"
                ),
                GifItem(
                    id = "2109",
                    title = "54.Deuses",
                    videoUrl = "https://media4.giphy.com/media/eXKh9sxTKmukEDgYid/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/eXKh9sxTKmukEDgYid/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/eXKh9sxTKmukEDgYid"
                ),
                GifItem(
                    id = "2110",
                    title = "55.Epístola",
                    videoUrl = "https://media4.giphy.com/media/N70ckaIqAQGf9dOmMO/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/N70ckaIqAQGf9dOmMO/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/N70ckaIqAQGf9dOmMO"
                ),
                GifItem(
                    id = "2111",
                    title = "56.Estátua",
                    videoUrl = "https://media4.giphy.com/media/Wgm8OKghmkiSlqCDMh/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Wgm8OKghmkiSlqCDMh/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Wgm8OKghmkiSlqCDMh"
                ),
                GifItem(
                    id = "2112",
                    title = "57.Eterno",
                    videoUrl = "https://media4.giphy.com/media/D987MR0tiEY4vE3dU6/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/D987MR0tiEY4vE3dU6/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/D987MR0tiEY4vE3dU6"
                ),
                GifItem(
                    id = "2113",
                    title = "58.Fariseu",
                    videoUrl = "https://media4.giphy.com/media/Qth5hef12q30ThTewP/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Qth5hef12q30ThTewP/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Qth5hef12q30ThTewP"
                ),
                GifItem(
                    id = "2114",
                    title = "59.Evangelismo",
                    videoUrl = "https://media4.giphy.com/media/SQzXofM89dxXhmxmmj/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/SQzXofM89dxXhmxmmj/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/SQzXofM89dxXhmxmmj"
                ),
                GifItem(
                    id = "2115",
                    title = "60.Fé",
                    videoUrl = "https://media4.giphy.com/media/uZtE3aett0upUE3KR2/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/uZtE3aett0upUE3KR2/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/uZtE3aett0upUE3KR2"
                ),
                GifItem(
                    id = "2116",
                    title = "61.Evangelho",
                    videoUrl = "https://media4.giphy.com/media/01MVJxeLP3xEzRHAn5/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/01MVJxeLP3xEzRHAn5/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/01MVJxeLP3xEzRHAn5"
                ),
                GifItem(
                    id = "2117",
                    title = "62.Eli sumo sacerdote",
                    videoUrl = "https://media4.giphy.com/media/ps1qpdnLy7xTvTjL9K/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ps1qpdnLy7xTvTjL9K/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ps1qpdnLy7xTvTjL9K"
                ),
                GifItem(
                    id = "2118",
                    title = "63",
                    videoUrl = "https://media4.giphy.com/media/yD9BSG8LU7H9rLXrZD/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/yD9BSG8LU7H9rLXrZD/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/yD9BSG8LU7H9rLXrZD"
                ),
                GifItem(
                    id = "2119",
                    title = "64.Éden",
                    videoUrl = "https://media4.giphy.com/media/p99dHAYZaRxfELexwX/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/p99dHAYZaRxfELexwX/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/p99dHAYZaRxfELexwX"
                ),
                GifItem(
                    id = "2120",
                    title = "65.Edom",
                    videoUrl = "https://media4.giphy.com/media/YUmB4xtrijgJO2w60s/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/YUmB4xtrijgJO2w60s/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/YUmB4xtrijgJO2w60s"
                ),
                GifItem(
                    id = "2121",
                    title = "66.Evangelista",
                    videoUrl = "https://media4.giphy.com/media/MtUukOFyQM2JKjEuRG/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/MtUukOFyQM2JKjEuRG/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/MtUukOFyQM2JKjEuRG"
                ),
                GifItem(
                    id = "2122",
                    title = "67.Eterno",
                    videoUrl = "https://media4.giphy.com/media/nM3hsvhT3RwKdQBobM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/nM3hsvhT3RwKdQBobM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/nM3hsvhT3RwKdQBobM"
                ),
                GifItem(
                    id = "2123",
                    title = "68.Expiação",
                    videoUrl = "https://media4.giphy.com/media/wzCaZUkvjKp9px3u5y/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/wzCaZUkvjKp9px3u5y/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/wzCaZUkvjKp9px3u5y"
                ),
                GifItem(
                    id = "2124",
                    title = "69.Escriba",
                    videoUrl = "https://media4.giphy.com/media/CCS8EmWZlZjZ34MuX5/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CCS8EmWZlZjZ34MuX5/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CCS8EmWZlZjZ34MuX5"
                ),
                GifItem(
                    id = "2125",
                    title = "70.Estrebaria",
                    videoUrl = "https://media4.giphy.com/media/eglMzDBDV1j6f3zo4g/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/eglMzDBDV1j6f3zo4g/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/eglMzDBDV1j6f3zo4g"
                ),
                GifItem(
                    id = "2126",
                    title = "71.Essênio",
                    videoUrl = "https://media4.giphy.com/media/ZsvipLIbiYcPPuuRa8/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ZsvipLIbiYcPPuuRa8/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ZsvipLIbiYcPPuuRa8"
                ),
                GifItem(
                    id = "2127",
                    title = "72.Galardão",
                    videoUrl = "https://media4.giphy.com/media/kXZRyTxnUU1KmLJ87j/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/kXZRyTxnUU1KmLJ87j/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/kXZRyTxnUU1KmLJ87j"
                ),
                GifItem(
                    id = "2128",
                    title = "73.Imoral",
                    videoUrl = "https://media4.giphy.com/media/8anjG3qHjKZmTY1jA9/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/8anjG3qHjKZmTY1jA9/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/8anjG3qHjKZmTY1jA9"
                ),
                GifItem(
                    id = "2129",
                    title = "74.Gigante",
                    videoUrl = "https://media4.giphy.com/media/E7zjXlaHi2M5ERh6Qt/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/E7zjXlaHi2M5ERh6Qt/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/E7zjXlaHi2M5ERh6Qt"
                ),
                GifItem(
                    id = "2130",
                    title = "75.Galardão",
                    videoUrl = "https://media4.giphy.com/media/pppkD8wBnykTzqtcWx/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/pppkD8wBnykTzqtcWx/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/pppkD8wBnykTzqtcWx"
                ),
                GifItem(
                    id = "2131",
                    title = "76.Honra",
                    videoUrl = "https://media4.giphy.com/media/3IJbApRuuXtXo48HUo/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/3IJbApRuuXtXo48HUo/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/3IJbApRuuXtXo48HUo"
                ),
                GifItem(
                    id = "2132",
                    title = "77.Iniquidade",
                    videoUrl = "https://media4.giphy.com/media/axMD1DxaYYGrLbcAoi/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/axMD1DxaYYGrLbcAoi/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/axMD1DxaYYGrLbcAoi"
                ),
                GifItem(
                    id = "2133",
                    title = "78.Holocausto",
                    videoUrl = "https://media4.giphy.com/media/mMT2A1Q98WZP7p4Rsb/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mMT2A1Q98WZP7p4Rsb/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mMT2A1Q98WZP7p4Rsb"
                ),
                GifItem(
                    id = "2134",
                    title = "79.Imperador",
                    videoUrl = "https://media4.giphy.com/media/MtX9g1EKQyrUKOKiBC/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/MtX9g1EKQyrUKOKiBC/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/MtX9g1EKQyrUKOKiBC"
                ),
                GifItem(
                    id = "2135",
                    title = "80.Ídolo",
                    videoUrl = "https://media4.giphy.com/media/uWRJZ6nLsMnaoA4bXO/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/uWRJZ6nLsMnaoA4bXO/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/uWRJZ6nLsMnaoA4bXO"
                ),
                GifItem(
                    id = "5030",
                    title = "81.Israel",
                    videoUrl = "https://media4.giphy.com/media/xILLIrELdGaDxzmbEy/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/xILLIrELdGaDxzmbEy/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/xILLIrELdGaDxzmbEy"
                ),
                GifItem(
                    id = "5031",
                    title = "82.Inferno",
                    videoUrl = "https://media4.giphy.com/media/e1fi1lDj5sz3ye656e/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/e1fi1lDj5sz3ye656e/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/e1fi1lDj5sz3ye656e"
                ),
                GifItem(
                    id = "5032",
                    title = "83.Império",
                    videoUrl = "https://media4.giphy.com/media/aIxY89csE4wkTTdNRv/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aIxY89csE4wkTTdNRv/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aIxY89csE4wkTTdNRv"
                ),
                GifItem(
                    id = "5033",
                    title = "84.Jericó",
                    videoUrl = "https://media4.giphy.com/media/6vhPATrlIBmNBJmrGW/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/6vhPATrlIBmNBJmrGW/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/6vhPATrlIBmNBJmrGW"
                ),
                GifItem(
                    id = "5034",
                    title = "85.Getsêmani",
                    videoUrl = "https://media4.giphy.com/media/iL0n8Rc95GG5Hjt9Y0/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/iL0n8Rc95GG5Hjt9Y0/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/iL0n8Rc95GG5Hjt9Y0"
                ),
                GifItem(
                    id = "5035",
                    title = "86.Graça",
                    videoUrl = "https://media4.giphy.com/media/QClh7nyKZIHdy3nNGB/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QClh7nyKZIHdy3nNGB/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QClh7nyKZIHdy3nNGB"
                ),
                GifItem(
                    id = "5036",
                    title = "87.Igreja",
                    videoUrl = "https://media4.giphy.com/media/X8BHhsJgnJZ2gItpsT/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/X8BHhsJgnJZ2gItpsT/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/X8BHhsJgnJZ2gItpsT"
                ),
                GifItem(
                    id = "5037",
                    title = "88.Geração",
                    videoUrl = "https://media4.giphy.com/media/bFUptEhenh5ouXBygS/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/bFUptEhenh5ouXBygS/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/bFUptEhenh5ouXBygS"
                ),
                GifItem(
                    id = "5038",
                    title = "89.Jordânia",
                    videoUrl = "https://media4.giphy.com/media/1VR8NyJyQTvr6gxMqZ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/1VR8NyJyQTvr6gxMqZ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/1VR8NyJyQTvr6gxMqZ"
                ),
                GifItem(
                    id = "5039",
                    title = "90.José do Egito",
                    videoUrl = "https://media4.giphy.com/media/S0dQ2XkNRyccKwkHOV/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/S0dQ2XkNRyccKwkHOV/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/S0dQ2XkNRyccKwkHOV"
                ),
                GifItem(
                    id = "5040",
                    title = "91.Judá",
                    videoUrl = "https://media4.giphy.com/media/HwoEBGAAOXlO6Q4wWo/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/HwoEBGAAOXlO6Q4wWo/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/HwoEBGAAOXlO6Q4wWo"
                ),
                GifItem(
                    id = "5041",
                    title = "92.Judeu",
                    videoUrl = "https://media4.giphy.com/media/vRTM386lnyanFJA7bJ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/vRTM386lnyanFJA7bJ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/vRTM386lnyanFJA7bJ"
                ),
                GifItem(
                    id = "5042",
                    title = "93.Lepra Leproso",
                    videoUrl = "https://media4.giphy.com/media/KvlZsRhdMC0gwqFMUv/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/KvlZsRhdMC0gwqFMUv/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/KvlZsRhdMC0gwqFMUv"
                ),
                GifItem(
                    id = "5043",
                    title = "94.Louvor",
                    videoUrl = "https://media4.giphy.com/media/TjgNydstWPLivi7IZ8/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/TjgNydstWPLivi7IZ8/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/TjgNydstWPLivi7IZ8"
                ),
                GifItem(
                    id = "5044",
                    title = "95.Justo",
                    videoUrl = "https://media4.giphy.com/media/629L6naw5zcFTZC2Ow/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/629L6naw5zcFTZC2Ow/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/629L6naw5zcFTZC2Ow"
                ),
                GifItem(
                    id = "5045",
                    title = "96.Lei",
                    videoUrl = "https://media4.giphy.com/media/Z46BBrsacmhRq2aSaq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Z46BBrsacmhRq2aSaq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Z46BBrsacmhRq2aSaq"
                ),
                GifItem(
                    id = "5046",
                    title = "97.Jerusalém",
                    videoUrl = "https://media4.giphy.com/media/fzIwjkvwjAzlsJGuZv/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/fzIwjkvwjAzlsJGuZv/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/fzIwjkvwjAzlsJGuZv"
                ),
                GifItem(
                    id = "5047",
                    title = "98.João Batista",
                    videoUrl = "https://media4.giphy.com/media/JdBr2BI71XTYc8JCIq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/JdBr2BI71XTYc8JCIq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/JdBr2BI71XTYc8JCIq"
                ),
                GifItem(
                    id = "5048",
                    title = "99.Judeia",
                    videoUrl = "https://media4.giphy.com/media/D9rcFGHBr7lwTsQR1y/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/D9rcFGHBr7lwTsQR1y/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/D9rcFGHBr7lwTsQR1y"
                ),
                GifItem(
                    id = "5049",
                    title = "100.Jordão",
                    videoUrl = "https://media4.giphy.com/media/aC6Y1GiOxLyduJXvDN/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aC6Y1GiOxLyduJXvDN/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aC6Y1GiOxLyduJXvDN"
                ),
                GifItem(
                    id = "5050",
                    title = "101.Juiz",
                    videoUrl = "https://media4.giphy.com/media/QGt3cWhnc5PmipsTmm/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QGt3cWhnc5PmipsTmm/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QGt3cWhnc5PmipsTmm"
                ),
                GifItem(
                    id = "5051",
                    title = "102.Justiça",
                    videoUrl = "https://media4.giphy.com/media/BzpLuWxnqFIT4bJXaa/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/BzpLuWxnqFIT4bJXaa/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/BzpLuWxnqFIT4bJXaa"
                ),
                GifItem(
                    id = "5052",
                    title = "103.Lázaro (história do rico)",
                    videoUrl = "https://media4.giphy.com/media/aDLznNt3zv0Jm5Fz3t/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aDLznNt3zv0Jm5Fz3t/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aDLznNt3zv0Jm5Fz3t"
                ),
                GifItem(
                    id = "5053",
                    title = "104.João Batista",
                    videoUrl = "https://media4.giphy.com/media/5dHwczlDzN83rSKL54/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/5dHwczlDzN83rSKL54/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/5dHwczlDzN83rSKL54"
                ),
                GifItem(
                    id = "5054",
                    title = "105.João Batista",
                    videoUrl = "https://media4.giphy.com/media/JtOlgBN1X4XeBlOuDU/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/JtOlgBN1X4XeBlOuDU/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/JtOlgBN1X4XeBlOuDU"
                ),
                GifItem(
                    id = "5055",
                    title = "106.Levitas",
                    videoUrl = "https://media4.giphy.com/media/sSysD8k7whAFzFx0ki/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/sSysD8k7whAFzFx0ki/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/sSysD8k7whAFzFx0ki"
                ),
                GifItem(
                    id = "5056",
                    title = "107.Maomé",
                    videoUrl = "https://media4.giphy.com/media/JVsoMiFsGkLnkU9PhJ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/JVsoMiFsGkLnkU9PhJ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/JVsoMiFsGkLnkU9PhJ"
                ),
                GifItem(
                    id = "5057",
                    title = "108.Muçulmano",
                    videoUrl = "https://media4.giphy.com/media/jqtAgckAvXpZjB047A/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/jqtAgckAvXpZjB047A/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/jqtAgckAvXpZjB047A"
                ),
                GifItem(
                    id = "5058",
                    title = "109.Mordecai",
                    videoUrl = "https://media4.giphy.com/media/jSzMTo2jENITGb02pj/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/jSzMTo2jENITGb02pj/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/jSzMTo2jENITGb02pj"
                ),
                GifItem(
                    id = "5059",
                    title = "110.Maná",
                    videoUrl = "https://media4.giphy.com/media/aC8v7P5nD2O081NRja/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aC8v7P5nD2O081NRja/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aC8v7P5nD2O081NRja"
                ),
                GifItem(
                    id = "5060",
                    title = "111.Menorá",
                    videoUrl = "https://media4.giphy.com/media/r6ef7SHbx2CIRhRrB6/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/r6ef7SHbx2CIRhRrB6/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/r6ef7SHbx2CIRhRrB6"
                ),
                GifItem(
                    id = "5061",
                    title = "112.Moral",
                    videoUrl = "https://media4.giphy.com/media/JDzndPr49W3qwkXzvI/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/JDzndPr49W3qwkXzvI/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/JDzndPr49W3qwkXzvI"
                ),
                GifItem(
                    id = "5062",
                    title = "113.Mesopotâmia",
                    videoUrl = "https://media4.giphy.com/media/Jgh6ZrmrRNcaaSpj3U/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Jgh6ZrmrRNcaaSpj3U/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Jgh6ZrmrRNcaaSpj3U"
                ),
                GifItem(
                    id = "5063",
                    title = "114.Misericórdia",
                    videoUrl = "https://media4.giphy.com/media/gzPt78hZ1ovJSb3gBi/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/gzPt78hZ1ovJSb3gBi/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/gzPt78hZ1ovJSb3gBi"
                ),
                GifItem(
                    id = "5064",
                    title = "115.Majestade",
                    videoUrl = "https://media4.giphy.com/media/3WxqKYAY9Ho9saFkiu/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/3WxqKYAY9Ho9saFkiu/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/3WxqKYAY9Ho9saFkiu"
                ),
                GifItem(
                    id = "5065",
                    title = "116.Misericórdia",
                    videoUrl = "https://media4.giphy.com/media/kIWtW2ZIEcQUYCIed2/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/kIWtW2ZIEcQUYCIed2/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/kIWtW2ZIEcQUYCIed2"
                ),
                GifItem(
                    id = "5066",
                    title = "117.Manjedoura",
                    videoUrl = "https://media4.giphy.com/media/DwVIsmAGodFaK3kwlY/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/DwVIsmAGodFaK3kwlY/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/DwVIsmAGodFaK3kwlY"
                ),
                GifItem(
                    id = "5067",
                    title = "118.Macedônia",
                    videoUrl = "https://media4.giphy.com/media/FRHvlFbq1ZkcFbIW05/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/FRHvlFbq1ZkcFbIW05/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/FRHvlFbq1ZkcFbIW05"
                ),
                GifItem(
                    id = "5068",
                    title = "119.Milagre",
                    videoUrl = "https://media4.giphy.com/media/2EGyDSnhyurJuY0iXy/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/2EGyDSnhyurJuY0iXy/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/2EGyDSnhyurJuY0iXy"
                ),
                GifItem(
                    id = "5069",
                    title = "120.Monte Sinai",
                    videoUrl = "https://media4.giphy.com/media/tuF6die5v67hzmeBIm/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/tuF6die5v67hzmeBIm/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/tuF6die5v67hzmeBIm"
                ),
                GifItem(
                    id = "8589",
                    title = "121.Mirra",
                    videoUrl = "https://media4.giphy.com/media/hybTpn46jIhZmoquMM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/hybTpn46jIhZmoquMM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/hybTpn46jIhZmoquMM"
                ),
                GifItem(
                    id = "8590",
                    title = "122.Obediência",
                    videoUrl = "https://media4.giphy.com/media/aRzfKVnbyl7CBJYZ9b/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aRzfKVnbyl7CBJYZ9b/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aRzfKVnbyl7CBJYZ9b"
                ),
                GifItem(
                    id = "8591",
                    title = "123.Natã",
                    videoUrl = "https://media4.giphy.com/media/o8KIVoIfDIqCR2yasx/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/o8KIVoIfDIqCR2yasx/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/o8KIVoIfDIqCR2yasx"
                ),
                GifItem(
                    id = "8592",
                    title = "124.Oração",
                    videoUrl = "https://media4.giphy.com/media/azkA5K9cQbLsisykf4/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/azkA5K9cQbLsisykf4/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/azkA5K9cQbLsisykf4"
                ),
                GifItem(
                    id = "8593",
                    title = "125.Pastor",
                    videoUrl = "https://media4.giphy.com/media/RDdkDUZQKqLaQOHDw8/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/RDdkDUZQKqLaQOHDw8/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/RDdkDUZQKqLaQOHDw8"
                ),
                GifItem(
                    id = "8594",
                    title = "126.Obediência",
                    videoUrl = "https://media4.giphy.com/media/ozMQBFYsOJVLOSaMZ9/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ozMQBFYsOJVLOSaMZ9/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ozMQBFYsOJVLOSaMZ9"
                ),
                GifItem(
                    id = "8595",
                    title = "127.Nínive",
                    videoUrl = "https://media4.giphy.com/media/kGmyP6Q26KuhIOcZtL/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/kGmyP6Q26KuhIOcZtL/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/kGmyP6Q26KuhIOcZtL"
                ),
                GifItem(
                    id = "8596",
                    title = "128.Páscoa",
                    videoUrl = "https://media4.giphy.com/media/5ysO5BuLCkW8tORHFE/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/5ysO5BuLCkW8tORHFE/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/5ysO5BuLCkW8tORHFE"
                ),
                GifItem(
                    id = "8597",
                    title = "129.Nazireu",
                    videoUrl = "https://media4.giphy.com/media/ogG3bqY84UYhm2SWJ9/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ogG3bqY84UYhm2SWJ9/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ogG3bqY84UYhm2SWJ9"
                ),
                GifItem(
                    id = "8598",
                    title = "130.Pastor",
                    videoUrl = "https://media4.giphy.com/media/ZtRsGUUh7RHIgy3UNw/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ZtRsGUUh7RHIgy3UNw/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ZtRsGUUh7RHIgy3UNw"
                ),
                GifItem(
                    id = "8599",
                    title = "131.Natal",
                    videoUrl = "https://media4.giphy.com/media/jQfvqK3pROKpEk82c6/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/jQfvqK3pROKpEk82c6/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/jQfvqK3pROKpEk82c6"
                ),
                GifItem(
                    id = "8600",
                    title = "132.Oferta",
                    videoUrl = "https://media4.giphy.com/media/LzIhvqy06VA3SDVegQ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/LzIhvqy06VA3SDVegQ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/LzIhvqy06VA3SDVegQ"
                ),
                GifItem(
                    id = "8601",
                    title = "133.Natal",
                    videoUrl = "https://media4.giphy.com/media/CfLW3cdD8rCG711tOn/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CfLW3cdD8rCG711tOn/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CfLW3cdD8rCG711tOn"
                ),
                GifItem(
                    id = "8602",
                    title = "134.Páscoa",
                    videoUrl = "https://media4.giphy.com/media/6DyTzLSqGV7Z1o1o82/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/6DyTzLSqGV7Z1o1o82/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/6DyTzLSqGV7Z1o1o82"
                ),
                GifItem(
                    id = "8603",
                    title = "135.Prosperar",
                    videoUrl = "https://media4.giphy.com/media/eJEvSkJp6TXwXQfeK3/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/eJEvSkJp6TXwXQfeK3/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/eJEvSkJp6TXwXQfeK3"
                ),
                GifItem(
                    id = "8604",
                    title = "136.Profecia",
                    videoUrl = "https://media4.giphy.com/media/LgR27sBpeg8AeUvzr2/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/LgR27sBpeg8AeUvzr2/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/LgR27sBpeg8AeUvzr2"
                ),
                GifItem(
                    id = "8605",
                    title = "137.Pecado",
                    videoUrl = "https://media4.giphy.com/media/dsm50a5GJL7eWeZ0fo/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/dsm50a5GJL7eWeZ0fo/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/dsm50a5GJL7eWeZ0fo"
                ),
                GifItem(
                    id = "8606",
                    title = "138.Prosperar",
                    videoUrl = "https://media4.giphy.com/media/OLPiMHTt5hgJ5Wdtvq/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/OLPiMHTt5hgJ5Wdtvq/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/OLPiMHTt5hgJ5Wdtvq"
                ),
                GifItem(
                    id = "8607",
                    title = "139.Publicano",
                    videoUrl = "https://media4.giphy.com/media/4iCKHSQhxwYKoik7I4/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/4iCKHSQhxwYKoik7I4/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/4iCKHSQhxwYKoik7I4"
                ),
                GifItem(
                    id = "8608",
                    title = "140.Pentateuco",
                    videoUrl = "https://media4.giphy.com/media/6c7psQndzEZTGp9yx3/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/6c7psQndzEZTGp9yx3/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/6c7psQndzEZTGp9yx3"
                ),
                GifItem(
                    id = "8609",
                    title = "141.Pentecostes",
                    videoUrl = "https://media4.giphy.com/media/xEEiTTpb4r4SwB0YT6/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/xEEiTTpb4r4SwB0YT6/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/xEEiTTpb4r4SwB0YT6"
                ),
                GifItem(
                    id = "8610",
                    title = "142.Profeta",
                    videoUrl = "https://media4.giphy.com/media/1pEcDVFdY9d0UUiIy0/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/1pEcDVFdY9d0UUiIy0/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/1pEcDVFdY9d0UUiIy0"
                ),
                GifItem(
                    id = "8611",
                    title = "143.Pregador",
                    videoUrl = "https://media4.giphy.com/media/ONUjqd9f0icWVEufqV/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ONUjqd9f0icWVEufqV/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ONUjqd9f0icWVEufqV"
                ),
                GifItem(
                    id = "8612",
                    title = "144.Profeta",
                    videoUrl = "https://media4.giphy.com/media/nGAtzv8ykA3OTpLjZJ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/nGAtzv8ykA3OTpLjZJ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/nGAtzv8ykA3OTpLjZJ"
                ),
                GifItem(
                    id = "8613",
                    title = "145.Pecador",
                    videoUrl = "https://media4.giphy.com/media/P2EqODyZ6F7L5znmEF/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/P2EqODyZ6F7L5znmEF/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/P2EqODyZ6F7L5znmEF"
                ),
                GifItem(
                    id = "8614",
                    title = "146.Paz",
                    videoUrl = "https://media4.giphy.com/media/Gui4WVeDn4YGPHt1Zn/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Gui4WVeDn4YGPHt1Zn/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Gui4WVeDn4YGPHt1Zn"
                ),
                GifItem(
                    id = "8615",
                    title = "147.Sacerdote",
                    videoUrl = "https://media4.giphy.com/media/6Oa3bSeeY1iNP4C2lw/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/6Oa3bSeeY1iNP4C2lw/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/6Oa3bSeeY1iNP4C2lw"
                ),
                GifItem(
                    id = "8616",
                    title = "148.Santo",
                    videoUrl = "https://media4.giphy.com/media/uZnA6DWtM1fqorOvYI/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/uZnA6DWtM1fqorOvYI/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/uZnA6DWtM1fqorOvYI"
                ),
                GifItem(
                    id = "8617",
                    title = "149.Salvador",
                    videoUrl = "https://media4.giphy.com/media/diAhjcOUV0PGRiXBzk/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/diAhjcOUV0PGRiXBzk/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/diAhjcOUV0PGRiXBzk"
                ),
                GifItem(
                    id = "8618",
                    title = "150.Salvar",
                    videoUrl = "https://media4.giphy.com/media/GaMbp9pr2G6MjeWReT/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/GaMbp9pr2G6MjeWReT/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/GaMbp9pr2G6MjeWReT"
                ),
                GifItem(
                    id = "9791",
                    title = "151.Ressuscitar",
                    videoUrl = "https://media4.giphy.com/media/qxJ4e3Y9ZZnMzZMTn5/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/qxJ4e3Y9ZZnMzZMTn5/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/qxJ4e3Y9ZZnMzZMTn5"
                ),
                GifItem(
                    id = "9792",
                    title = "152.Religião",
                    videoUrl = "https://media4.giphy.com/media/tWkYN8GHr6uPZmnQHc/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/tWkYN8GHr6uPZmnQHc/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/tWkYN8GHr6uPZmnQHc"
                ),
                GifItem(
                    id = "9793",
                    title = "153.Saduceu",
                    videoUrl = "https://media4.giphy.com/media/6QquA8jYvitq44uALf/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/6QquA8jYvitq44uALf/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/6QquA8jYvitq44uALf"
                ),
                GifItem(
                    id = "9794",
                    title = "154.Santo",
                    videoUrl = "https://media4.giphy.com/media/NUSrJ3hESAm1E7axyN/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/NUSrJ3hESAm1E7axyN/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/NUSrJ3hESAm1E7axyN"
                ),
                GifItem(
                    id = "9795",
                    title = "155.Sinagoga",
                    videoUrl = "https://media4.giphy.com/media/x21lSNos3gZrIPLdal/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/x21lSNos3gZrIPLdal/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/x21lSNos3gZrIPLdal"
                ),
                GifItem(
                    id = "9796",
                    title = "156.Tiago (irmão de Jesus)",
                    videoUrl = "https://media4.giphy.com/media/GVP0zQHST5EV1jW4qm/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/GVP0zQHST5EV1jW4qm/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/GVP0zQHST5EV1jW4qm"
                ),
                GifItem(
                    id = "9797",
                    title = "157.Sumo Sacerdote",
                    videoUrl = "https://media4.giphy.com/media/Qu8jZp5gWbGIKVsTAM/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/Qu8jZp5gWbGIKVsTAM/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/Qu8jZp5gWbGIKVsTAM"
                ),
                GifItem(
                    id = "9798",
                    title = "158.Templo",
                    videoUrl = "https://media4.giphy.com/media/nAsg17X8oOYkbfrlt0/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/nAsg17X8oOYkbfrlt0/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/nAsg17X8oOYkbfrlt0"
                ),
                GifItem(
                    id = "9799",
                    title = "159.Sodoma e Gomorra",
                    videoUrl = "https://media4.giphy.com/media/SMoQsD90iCYNZ9kIK8/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/SMoQsD90iCYNZ9kIK8/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/SMoQsD90iCYNZ9kIK8"
                ),
                GifItem(
                    id = "9800",
                    title = "160.Tentação",
                    videoUrl = "https://media4.giphy.com/media/kx6u8phTx8uERvpm6f/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/kx6u8phTx8uERvpm6f/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/kx6u8phTx8uERvpm6f"
                ),
                GifItem(
                    id = "9801",
                    title = "161.Sinédrio",
                    videoUrl = "https://media4.giphy.com/media/8zAauN1bFdSHsTCP6J/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/8zAauN1bFdSHsTCP6J/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/8zAauN1bFdSHsTCP6J"
                ),
                GifItem(
                    id = "9802",
                    title = "162.Tentação",
                    videoUrl = "https://media4.giphy.com/media/mSZxrZffmLKc8S7Dbk/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mSZxrZffmLKc8S7Dbk/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mSZxrZffmLKc8S7Dbk"
                ),
                GifItem(
                    id = "9803",
                    title = "163.Visão",
                    videoUrl = "https://media4.giphy.com/media/NzyufFf0JiFweKuWbB/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/NzyufFf0JiFweKuWbB/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/NzyufFf0JiFweKuWbB"
                ),
                GifItem(
                    id = "9804",
                    title = "164.Versículo",
                    videoUrl = "https://media4.giphy.com/media/EAAabDsmKuxbn0QGw4/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/EAAabDsmKuxbn0QGw4/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/EAAabDsmKuxbn0QGw4"
                ),
                GifItem(
                    id = "9805",
                    title = "165.Trindade",
                    videoUrl = "https://media4.giphy.com/media/KGCa7e76vkSU151JCB/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/KGCa7e76vkSU151JCB/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/KGCa7e76vkSU151JCB"
                ),
                GifItem(
                    id = "9806",
                    title = "166.Unção",
                    videoUrl = "https://media4.giphy.com/media/8DKdWLxQIWzV0wFnjP/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/8DKdWLxQIWzV0wFnjP/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/8DKdWLxQIWzV0wFnjP"
                ),
                GifItem(
                    id = "9807",
                    title = "167.Trono",
                    videoUrl = "https://media4.giphy.com/media/q9U2uqAebBKXecJEmN/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/q9U2uqAebBKXecJEmN/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/q9U2uqAebBKXecJEmN"
                ),
                GifItem(
                    id = "9808",
                    title = "168.Tribo",
                    videoUrl = "https://media4.giphy.com/media/VsA5y2LM8i9aSr83SA/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/VsA5y2LM8i9aSr83SA/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/VsA5y2LM8i9aSr83SA"
                ),
                GifItem(
                    id = "9809",
                    title = "169.Tribo",
                    videoUrl = "https://media4.giphy.com/media/yBnDsYg7320lVDR3VR/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/yBnDsYg7320lVDR3VR/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/yBnDsYg7320lVDR3VR"
                ),
                GifItem(
                    id = "9810",
                    title = "170.Raquel",
                    videoUrl = "https://media4.giphy.com/media/y1mvJc1ocjq00xaxqf/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/y1mvJc1ocjq00xaxqf/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/y1mvJc1ocjq00xaxqf"
                ),
                GifItem(
                    id = "9811",
                    title = "171.Salomão",
                    videoUrl = "https://media4.giphy.com/media/aIGsPYUDuxa8CbijVD/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/aIGsPYUDuxa8CbijVD/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/aIGsPYUDuxa8CbijVD"
                ),
                GifItem(
                    id = "9812",
                    title = "172.Tomé",
                    videoUrl = "https://media4.giphy.com/media/hey6b4DRl2pYEragIF/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/hey6b4DRl2pYEragIF/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/hey6b4DRl2pYEragIF"
                ),
                GifItem(
                    id = "9813",
                    title = "173.Samaria",
                    videoUrl = "https://media4.giphy.com/media/7FkfT8zjlmJPio9kEJ/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/7FkfT8zjlmJPio9kEJ/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/7FkfT8zjlmJPio9kEJ"
                ),
                GifItem(
                    id = "9814",
                    title = "174.Samaritano",
                    videoUrl = "https://media4.giphy.com/media/S8QGeP3HAk9qsM7MaA/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/S8QGeP3HAk9qsM7MaA/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/S8QGeP3HAk9qsM7MaA"
                ),
                GifItem(
                    id = "9815",
                    title = "175.Pilatos",
                    videoUrl = "https://media4.giphy.com/media/ooxEPVVau3KxNxARXT/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ooxEPVVau3KxNxARXT/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ooxEPVVau3KxNxARXT"
                ),
                GifItem(
                    id = "9816",
                    title = "176.Silas",
                    videoUrl = "https://media4.giphy.com/media/CY90YCDaNl1yNtpth3/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CY90YCDaNl1yNtpth3/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CY90YCDaNl1yNtpth3"
                ),
                GifItem(
                    id = "9817",
                    title = "177.Rebeca",
                    videoUrl = "https://media4.giphy.com/media/KYnAxbdrGeXcqGpNfh/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/KYnAxbdrGeXcqGpNfh/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/KYnAxbdrGeXcqGpNfh"
                ),
                GifItem(
                    id = "9818",
                    title = "178.Vasti",
                    videoUrl = "https://media4.giphy.com/media/3Q04bdErlhi1rGGGiY/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/3Q04bdErlhi1rGGGiY/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/3Q04bdErlhi1rGGGiY"
                ),
                GifItem(
                    id = "9819",
                    title = "179.Saul",
                    videoUrl = "https://media4.giphy.com/media/CLcEeYZkb9RhkIGGTr/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CLcEeYZkb9RhkIGGTr/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CLcEeYZkb9RhkIGGTr"
                ),
                GifItem(
                    id = "9820",
                    title = "180.Zaqueu",
                    videoUrl = "https://media4.giphy.com/media/mu03ySRJ9tiulHNs9H/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/mu03ySRJ9tiulHNs9H/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/mu03ySRJ9tiulHNs9H"
                ),
                GifItem(
                    id = "76678",
                    title = "181.Sansão",
                    videoUrl = "https://media4.giphy.com/media/bXnUf5R0h3naHlXb7U/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/bXnUf5R0h3naHlXb7U/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/bXnUf5R0h3naHlXb7U"
                ),
                GifItem(
                    id = "76679",
                    title = "182.Safira",
                    videoUrl = "https://media4.giphy.com/media/niRzYSvh1ZbcRde857/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/niRzYSvh1ZbcRde857/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/niRzYSvh1ZbcRde857"
                ),
                GifItem(
                    id = "76680",
                    title = "183.Zacarias",
                    videoUrl = "https://media4.giphy.com/media/92WyAPaWisXfgWVVGB/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/92WyAPaWisXfgWVVGB/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/92WyAPaWisXfgWVVGB"
                ),
                GifItem(
                    id = "76681",
                    title = "184.Uzias",
                    videoUrl = "https://media4.giphy.com/media/0CNI6MxLwa7qWskTKF/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/0CNI6MxLwa7qWskTKF/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/0CNI6MxLwa7qWskTKF"
                ),
                GifItem(
                    id = "76682",
                    title = "185.Urias",
                    videoUrl = "https://media4.giphy.com/media/uiVbVCN5x2CfAhjybL/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/uiVbVCN5x2CfAhjybL/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/uiVbVCN5x2CfAhjybL"
                ),
                GifItem(
                    id = "76683",
                    title = "186.Raabe",
                    videoUrl = "https://media4.giphy.com/media/SgkDNguT3vjMFhtDm3/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/SgkDNguT3vjMFhtDm3/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/SgkDNguT3vjMFhtDm3"
                ),
                GifItem(
                    id = "76684",
                    title = "187.Priscila",
                    videoUrl = "https://media4.giphy.com/media/N2V2qbyFUsc4h9tnTt/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/N2V2qbyFUsc4h9tnTt/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/N2V2qbyFUsc4h9tnTt"
                ),
                GifItem(
                    id = "76685",
                    title = "188.Sara",
                    videoUrl = "https://media4.giphy.com/media/CLYTWWyaoY9QS9tg9Z/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/CLYTWWyaoY9QS9tg9Z/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/CLYTWWyaoY9QS9tg9Z"
                ),
                GifItem(
                    id = "76686",
                    title = "189.Pródigo (filho pródigo)",
                    videoUrl = "https://media4.giphy.com/media/D6fUPkM5FqyCngbBcI/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/D6fUPkM5FqyCngbBcI/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/D6fUPkM5FqyCngbBcI"
                ),
                GifItem(
                    id = "76687",
                    title = "190.Messias",
                    videoUrl = "https://media4.giphy.com/media/cdBwekV9TM5ol3JRLV/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/cdBwekV9TM5ol3JRLV/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/cdBwekV9TM5ol3JRLV"
                ),
                GifItem(
                    id = "76688",
                    title = "191.Miriã",
                    videoUrl = "https://media4.giphy.com/media/ipoQZwC1SxWltlTf2j/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/ipoQZwC1SxWltlTf2j/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/ipoQZwC1SxWltlTf2j"
                ),
                GifItem(
                    id = "76689",
                    title = "192.Noé",
                    videoUrl = "https://media4.giphy.com/media/vYqkOYatupfI4AxoCl/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/vYqkOYatupfI4AxoCl/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/vYqkOYatupfI4AxoCl"
                ),
                GifItem(
                    id = "76690",
                    title = "193.Nicodemus",
                    videoUrl = "https://media4.giphy.com/media/nFvcFsZA0ZBrgH5m1k/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/nFvcFsZA0ZBrgH5m1k/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/nFvcFsZA0ZBrgH5m1k"
                ),
                GifItem(
                    id = "76691",
                    title = "194.Melquisedeque",
                    videoUrl = "https://media4.giphy.com/media/UlTzJXi95TC41FlEpm/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/UlTzJXi95TC41FlEpm/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/UlTzJXi95TC41FlEpm"
                ),
                GifItem(
                    id = "76692",
                    title = "195.Potifar",
                    videoUrl = "https://media4.giphy.com/media/8wcLS3ZfnAe52jHavB/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/8wcLS3ZfnAe52jHavB/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/8wcLS3ZfnAe52jHavB"
                ),
                GifItem(
                    id = "76693",
                    title = "196.Parábola",
                    videoUrl = "https://media4.giphy.com/media/l5Olkri2bGl33N6CwF/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/l5Olkri2bGl33N6CwF/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/l5Olkri2bGl33N6CwF"
                ),
                GifItem(
                    id = "76694",
                    title = "197.Paulo",
                    videoUrl = "https://media4.giphy.com/media/dCeiZqakMQb9WHVXVW/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/dCeiZqakMQb9WHVXVW/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/dCeiZqakMQb9WHVXVW"
                ),
                GifItem(
                    id = "76695",
                    title = "198.Nabucodonosor",
                    videoUrl = "https://media4.giphy.com/media/QPVClPqzlzRBg11wOK/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/QPVClPqzlzRBg11wOK/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/QPVClPqzlzRBg11wOK"
                ),
                GifItem(
                    id = "76696",
                    title = "199.Noemi",
                    videoUrl = "https://media4.giphy.com/media/BOd8yQ7BbIz3uO2jVH/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/BOd8yQ7BbIz3uO2jVH/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/BOd8yQ7BbIz3uO2jVH"
                ),
                GifItem(
                    id = "76697",
                    title = "200.José",
                    videoUrl = "https://media4.giphy.com/media/sdsuC0s96kM1wQQdQr/giphy.mp4",
                    thumbnailUrl = "https://media4.giphy.com/media/sdsuC0s96kM1wQQdQr/giphy.gif",
                    shareUrl = "https://giphy.com/gifs/sdsuC0s96kM1wQQdQr"
                ),
            )
        )
    }
}