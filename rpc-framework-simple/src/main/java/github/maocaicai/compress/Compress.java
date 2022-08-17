package github.maocaicai.compress;

import github.maocaicai.extention.SPI;

/**
 * @author maocaicai
 * since 2022/8/17 14:39
 */
@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
