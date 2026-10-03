#include <iostream>
#include <vector>
#include <iomanip>
using namespace std;
struct Vuln {
    char buf[32];
    vector<int> vec;
};
int main() {
    Vuln v;
    for(int i=0; i<32; i++) v.buf[i] = 'X';
    v.vec.push_back(1);
    cout << "v size: " << v.vec.size() << " cap: " << v.vec.capacity() << endl;
    cout << "enter: ";
    cin >> v.buf;
    cout << "v size: " << v.vec.size() << " cap: " << v.vec.capacity() << endl;
    cout << "buf: ";
    unsigned char* p = (unsigned char*)&v;
    for(int i=0; i<56; i++) {
        cout << hex << setfill('0') << setw(2) << (int)p[i] << " ";
    }
    cout << endl;
    return 0;
}
