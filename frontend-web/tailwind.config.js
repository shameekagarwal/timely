module.exports = {
  prefix: "",
  purge: {
    enabled: !!process.env.production,
    content: ["./src/**/*.{html,ts}"],
  },
  darkMode: "class",
  theme: {
    extend: {},
  },
  variants: {
    extend: {},
  },
  plugins: [],
};
