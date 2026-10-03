#include <iostream>
#include <ostream>
#include <vector>

class Vuln {
public:
  char buf[32];
  std::vector<unsigned long long> vec;

  void read_to_buffer() {
    std::cout << "enter to buffer: ";
    std::cin >> buf;
  }

  void insert_to_vector() {
    unsigned long long x;
    std::cout << "insert to vector: ";
    std::cin >> x;
    vec.push_back(x);
  }

  void dump_vector(bool &udh_nyontek) {
    if (udh_nyontek) {
      std::cout << "udah banhhhh" << std::endl;
      return;
    }
    udh_nyontek = true;
    for (auto i : vec) {
      std::cout << std::hex << i << std::endl;
    }
  }
};

__attribute__((constructor)) void disable_buffering() {
  setvbuf(stdin, NULL, _IONBF, 0);
  setvbuf(stdout, NULL, _IONBF, 0);
  setvbuf(stderr, NULL, _IONBF, 0);
}

int main() {
  Vuln obj;
  short perintah;
  bool udh_nyontek = false;

  std::cout << "Soal direcycle udah 2 kali coyyy" << std::endl;

  while (1) {
    std::cout << ">> ";
    std::cin >> perintah;

    switch (perintah) {
    case 1:
      obj.read_to_buffer();
      break;
    case 2:
      obj.insert_to_vector();
      break;
    case 3:
      obj.dump_vector(udh_nyontek);
      break;
    default:
      std::cout << "ngapain heyyyyyy" << std::endl;
      exit(69);
    }
  }
}
